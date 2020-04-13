package com.github.shynixn.youtube2resourcepacksongs.logic.service

import com.github.kiulian.downloader.OnYoutubeDownloadListener
import com.github.kiulian.downloader.YoutubeDownloader
import com.github.shynixn.youtube2resourcepacksongs.api.entity.Progress
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.YoutubeVideoDownloadService
import com.github.shynixn.youtube2resourcepacksongs.logic.entity.Video
import org.apache.commons.io.FileUtils
import java.io.File
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
    override fun download(video: Video, targetFolder: Path, progressFunction: (Progress) -> Unit) {
        val name = video.videoPathInResourcePack.split("/").last()
        val currentFolder = Paths.get("")
        val downloadFolder = currentFolder.resolve("download-" + UUID.randomUUID().toString().split("-").first())

        if (Files.exists(downloadFolder)) {
            FileUtils.deleteDirectory(downloadFolder.toFile())
        }

        Files.createDirectories(downloadFolder)
        val downloadFile = targetFolder.resolve("$name.mp4")
        Files.deleteIfExists(downloadFile)

        val youtubeId = getYoutubeId(video.videoUrl)
        val youtubeDownloader = YoutubeDownloader()
        val ytVideo = youtubeDownloader.getVideo(youtubeId)
        var hasFinished = false

        ytVideo.downloadAsync(ytVideo.audioFormats()[0], downloadFolder.toFile(), object : OnYoutubeDownloadListener {
            override fun onDownloading(progress: Int) {
                progressFunction.invoke(Progress(progress, "Downloading '" + name + "' from " + video.videoUrl + "..."))
            }

            override fun onFinished(file: File?) {
                hasFinished = true
                progressFunction.invoke(Progress(100, "Downloaded '" + name + "' from " + video.videoUrl + "."))
            }

            override fun onError(throwable: Throwable?) {
                hasFinished = true
            }
        })

        while (!hasFinished) {
            Thread.sleep(500)
        }

        FileUtils.moveFile(downloadFolder.toFile().listFiles()!!.first(), downloadFile.toFile())
        FileUtils.deleteDirectory(downloadFolder.toFile())
    }

    /**
     * Gets the youtubeId from the given url.
     */
    private fun getYoutubeId(url: String): String {
        val builder = StringBuilder()
        var index = url.indexOf("v=") + 2
        while (index < url.length) {
            val character = url[index]

            if (character == '&') {
                break
            }

            builder.append(character)
            index++
        }

        return builder.toString()
    }
}