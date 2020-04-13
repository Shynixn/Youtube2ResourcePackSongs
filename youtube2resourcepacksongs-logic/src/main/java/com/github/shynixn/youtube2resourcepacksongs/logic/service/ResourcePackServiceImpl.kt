@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.youtube2resourcepacksongs.logic.service

import com.github.shynixn.youtube2resourcepacksongs.api.entity.Progress
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.CsvFileService
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.FFmpegService
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.ResourcePackService
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.YoutubeVideoDownloadService
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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

        if (!Files.exists(songsFolder)) {
            Files.createDirectories(songsFolder)
        }

        val videos = csvFileService.parseFile(inputFile)

        for (video in videos) {
            youtubeVideoDownloadService.download(video, songsFolder, progressFunction)
        }

        for (file in songsFolder.toFile().listFiles()!!.filter { e -> e.name.endsWith(".mp4") }) {
            val oggFile = file.toPath().parent.resolve(file.nameWithoutExtension + ".ogg")
            Files.deleteIfExists(oggFile)
            fFmpegService.convertToOgg(file.toPath(), progressFunction)
        }

        progressFunction.invoke(Progress(0, "Finished generating resource pack."))
    }
}