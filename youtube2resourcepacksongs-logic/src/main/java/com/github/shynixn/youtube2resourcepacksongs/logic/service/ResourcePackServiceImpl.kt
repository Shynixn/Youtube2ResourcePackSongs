@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.youtube2resourcepacksongs.logic.service

import com.github.shynixn.youtube2resourcepacksongs.api.entity.Progress
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.CsvFileService
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.FFmpegService
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.ResourcePackService
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.YoutubeVideoDownloadService
import com.github.shynixn.youtube2resourcepacksongs.logic.entity.Video
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

/**
 * Created by Shynixn 2020.
 * <p>
 * Version 1.5
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2020 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class ResourcePackServiceImpl(
    private val youtubeVideoDownloadService: YoutubeVideoDownloadService = YoutubeVideoDownloadServiceImpl(),
    private val fFmpegService: FFmpegService = FfmpegServiceImpl(),
    private val csvFileService: CsvFileService = CsvFileServiceImpl()
) :
    ResourcePackService {
    /**
     * Generates a resource pack.
     */
    override fun generateResourcePack(inputFile: Path, outputFile: Path, progressF: Any) {
        val progressFunction = progressF as ((Progress) -> Unit)
        progressFunction.invoke(Progress(0, "Generating resource pack..."))

        val songsFolder = Paths.get("songs")

        if (Files.exists(songsFolder)) {
            FileUtils.deleteDirectory(songsFolder.toFile())
        }

        Files.createDirectories(songsFolder)

        val videos = csvFileService.parseFile(inputFile).toList()

        for (video in videos) {
            youtubeVideoDownloadService.download(video, songsFolder, progressFunction)
        }

        for (file in songsFolder.toFile().listFiles()!!.filter { e -> e.name.endsWith(".mp4") }) {
            val oggFile = file.toPath().parent.resolve(file.nameWithoutExtension + ".ogg")
            Files.deleteIfExists(oggFile)
            fFmpegService.convertToOgg(file.toPath(), progressFunction)
        }

        progressFunction.invoke(Progress(50, "Packing resource pack..."))
        generateResourcePackDirectoryStructure(videos)
        zipResourcePackDirectoryStructure()
        progressFunction.invoke(Progress(100, "Finished generating resource pack."))
    }

    /**
     * Generates the zip file.
     */
    private fun zipResourcePackDirectoryStructure() {
        val resourcePackFolder = Paths.get("resourcepack")
        val resourcePackZipFile = Paths.get("").resolve("resourcepack.zip")
        Files.deleteIfExists(resourcePackZipFile)

        val zipFile = ZipFile(resourcePackZipFile.toFile())
        zipFile.addFolder(resourcePackFolder.resolve("assets").toFile())
        zipFile.addFile(resourcePackFolder.resolve("pack.mcmeta").toFile())
        zipFile.addFile(resourcePackFolder.resolve("pack.png").toFile())

        FileUtils.deleteDirectory(resourcePackFolder.toFile())
    }

    /**
     * Generates the structure of the resource pack.
     */
    private fun generateResourcePackDirectoryStructure(videos: List<Video>) {
        val resourcePackFolder = Paths.get("resourcepack")

        if (Files.exists(resourcePackFolder)) {
            FileUtils.deleteDirectory(resourcePackFolder.toFile())
        }

        Files.createDirectories(resourcePackFolder)

        Files.writeString(
            resourcePackFolder.resolve("pack.mcmeta"),
            "{\n" +
                    "\t\"pack\": {\n" +
                    "\t\t\"pack_format\": 3,\n" +
                    "\t\t\"description\": \"Generated sound resource pack.\"\n" +
                    "\t}\n" +
                    "}"
        )
        Thread.currentThread().contextClassLoader.getResourceAsStream("pack.png").use { input ->
            FileOutputStream(resourcePackFolder.resolve("pack.png").toFile()).use { output ->
                IOUtils.copy(input, output)
            }
        }

        val assetsFolder = resourcePackFolder.resolve("assets")
        Files.createDirectories(assetsFolder)
        val minecraftFolder = assetsFolder.resolve("minecraft")
        Files.createDirectories(minecraftFolder)
        val soundsFolder = minecraftFolder.resolve("sounds")
        Files.createDirectories(minecraftFolder)

        val soundConfiguration = StringBuilder()
        soundConfiguration.appendln("{")

        var isFirstLine = true

        for (video in videos) {
            val structures = video.videoPathInResourcePack.split("/").toMutableList()
            val musicName = video.videoPathInResourcePack.split("/").last()
            structures.removeAt(structures.size - 1)
            val oggFile = Paths.get("songs").resolve("$musicName.ogg")
            FileUtils.moveFileToDirectory(
                oggFile.toFile(),
                soundsFolder.resolve(structures.joinToString("")).toFile(),
                true
            )

            if (!isFirstLine) {
                soundConfiguration.append(",")
                soundConfiguration.appendln()
            }

            isFirstLine = false

            soundConfiguration.appendln("   \"${video.videoPathInResourcePack.replace("/", ".")}\": {")
            soundConfiguration.appendln("   \"category\": \"master\",")
            soundConfiguration.appendln("   \"sounds\": [")
            soundConfiguration.appendln("       {")
            soundConfiguration.appendln("           \"name\": \"${video.videoPathInResourcePack}\",")
            soundConfiguration.appendln("           \"stream\": true")
            soundConfiguration.appendln("       }")
            soundConfiguration.appendln("    ]")
            soundConfiguration.appendln("  }")
        }

        soundConfiguration.appendln("}")
        Files.writeString(minecraftFolder.resolve("sounds.json"), soundConfiguration.toString())
    }
}