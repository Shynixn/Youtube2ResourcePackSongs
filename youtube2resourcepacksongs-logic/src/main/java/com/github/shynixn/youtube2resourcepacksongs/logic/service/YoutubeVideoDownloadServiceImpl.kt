package com.github.shynixn.youtube2resourcepacksongs.logic.service

import com.github.kiulian.downloader.YoutubeDownloader
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.YoutubeVideoDownloadService
import org.apache.commons.io.FileUtils
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

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
class YoutubeVideoDownloadServiceImpl : YoutubeVideoDownloadService {
    /**
     * Downloads the given videoUrl.
     */
    override fun download(videoUrl: String, name: String): Path {
        val currentFolder = Paths.get("")
        val downloadFolder = currentFolder.resolve("download-" + UUID.randomUUID().toString().split("-").first())

        if (Files.exists(downloadFolder)) {
            FileUtils.deleteDirectory(downloadFolder.toFile())
        }

        Files.createDirectories(downloadFolder)
        val downloadFile = downloadFolder.resolve("$name.mp4")

        val youtubeId = getYoutubeId(videoUrl)
        val v = YoutubeDownloader();
        val video = v.getVideo(youtubeId)
        video.download(video.audioFormats()[0], downloadFolder.toFile())
        FileUtils.moveFile(downloadFolder.toFile().listFiles()!!.first(), downloadFile.toFile())
        return downloadFile
    }

    /**
     * Gets the youtubeId from the given url.
     */
    private fun getYoutubeId(url: String): String {
        val builder = StringBuilder()
        var index = url.indexOf("v=") + 2
        while (index < url.length) {
            val character = url.get(index)

            if (character == '&') {
                break
            }

            builder.append(character)
            index++
        }

        return builder.toString()
    }
}