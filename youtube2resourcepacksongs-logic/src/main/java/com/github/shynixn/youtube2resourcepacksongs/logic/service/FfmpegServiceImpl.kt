package com.github.shynixn.youtube2resourcepacksongs.logic.service

import com.github.shynixn.youtube2resourcepacksongs.api.entity.Progress
import com.github.shynixn.youtube2resourcepacksongs.logic.contract.FFmpegService
import com.github.shynixn.youtube2resourcepacksongs.logic.entity.OSArchitectureTypes
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.progress.ProgressListener
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

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
class FfmpegServiceImpl : FFmpegService {
    private val ffmpegBinaries = mapOf(
        OSArchitectureTypes.WINDOWS_32 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-win-32.zip",
        OSArchitectureTypes.WINDOWS_64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-win-64.zip",
        OSArchitectureTypes.LINUX_32 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-linux-32.zip",
        OSArchitectureTypes.LINUX_64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-linux-64.zip",
        OSArchitectureTypes.LINUX_ARMHF to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-linux-armhf-32.zip",
        OSArchitectureTypes.LINUX_ARMEL to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-linux-armel-32.zip",
        OSArchitectureTypes.LINUX_ARM64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-linux-arm-64.zip",
        OSArchitectureTypes.OSX_64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-osx-64.zip"
    )

    private val ffProbeBinaries = mapOf(
        OSArchitectureTypes.WINDOWS_32 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-win-32.zip",
        OSArchitectureTypes.WINDOWS_64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-win-64.zip",
        OSArchitectureTypes.LINUX_32 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-linux-32.zip",
        OSArchitectureTypes.LINUX_64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-linux-64.zip",
        OSArchitectureTypes.LINUX_ARMHF to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-linux-armhf-32.zip",
        OSArchitectureTypes.LINUX_ARMEL to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-linux-armel-32.zip",
        OSArchitectureTypes.LINUX_ARM64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-linux-arm-64.zip",
        OSArchitectureTypes.OSX_64 to "https://github.com/vot/ffbinaries-prebuilt/releases/download/v4.2.1/ffprobe-4.2.1-osx-64.zip"
    )

    /**
     * Converts the given videoFile to an ogg file.
     */
    override fun convertToOgg(videoFile: Path, progressFunction: (Progress) -> Unit) {
        var success = false

        val ffmpegRootFolder = Paths.get("ffmpeg")

        if (!Files.exists(ffmpegRootFolder)) {
            Files.createDirectories(ffmpegRootFolder)
        }

        val architectures = getArchitectures()
        var i = 0

        while (!success) {
            val architecture = architectures[i]
            val ffmFolder = checkAndInstall(architecture, progressFunction)

            val outPutPathMp3 =
                videoFile.parent.toFile().absolutePath + "/" + videoFile.toFile().nameWithoutExtension + ".mp3"
            val outPutPathOgg =
                videoFile.parent.toFile().absolutePath + "/" + videoFile.toFile().nameWithoutExtension + ".ogg"
            try {
                convertInputToOutPut(ffmFolder, videoFile.toFile().absolutePath, outPutPathMp3, progressFunction)
                convertInputToOutPut(ffmFolder, outPutPathMp3, outPutPathOgg, progressFunction)
                success = true
                FileUtils.write(ffmpegRootFolder.resolve("os.dat").toFile(), architecture.identififer, "UTF-8")

                for (file in ffmpegRootFolder.toFile().listFiles()!!.filter { e -> e.name != architecture.identififer && e.name != "os.dat" }) {
                    FileUtils.deleteDirectory(file)
                }
            } catch (e: Exception) {
            }
            i++
        }
    }

    /**
     * Checks if the given architecture is installed or installs it.
     */
    private fun checkAndInstall(osArchitectureTypes: OSArchitectureTypes, progressFunction: (Progress) -> Unit): Path {
        val ffmpegFolder = Paths.get("ffmpeg")
        val installationFolder = ffmpegFolder.resolve(osArchitectureTypes.identififer)

        if (Files.exists(installationFolder)) {
            return installationFolder
        }

        val downloadedffmpeg =
            downloadFileFromUrl(ffmpegFolder, ffmpegBinaries.getValue(osArchitectureTypes), progressFunction)
        Files.createDirectories(installationFolder)
        FileUtils.moveFileToDirectory(downloadedffmpeg.toFile(), installationFolder.toFile(), true)
        val downloadedffprobe =
            downloadFileFromUrl(ffmpegFolder, ffProbeBinaries.getValue(osArchitectureTypes), progressFunction)
        FileUtils.moveFileToDirectory(downloadedffprobe.toFile(), installationFolder.toFile(), true)
        FileUtils.deleteDirectory(ffmpegFolder.resolve("download").toFile())

        return installationFolder
    }

    /**
     * Gets the next architectures.
     */
    private fun getArchitectures(): List<OSArchitectureTypes> {
        val ffmpegFolder = Paths.get("ffmpeg")

        val architectureInfoFile = ffmpegFolder.resolve("os.dat")

        if (Files.exists(architectureInfoFile)) {
            val architectureInfo = Files.readString(architectureInfoFile)
            val architecture = OSArchitectureTypes.values().first { e -> e.identififer == architectureInfo }
            return listOf(architecture)
        }

        return listOf(
            OSArchitectureTypes.OSX_64,
            OSArchitectureTypes.WINDOWS_64,
            OSArchitectureTypes.LINUX_64,
            OSArchitectureTypes.WINDOWS_32,
            OSArchitectureTypes.LINUX_32,
            OSArchitectureTypes.LINUX_ARM64,
            OSArchitectureTypes.LINUX_ARMEL,
            OSArchitectureTypes.LINUX_ARMHF
        )
    }

    private fun convertInputToOutPut(
        ffmFolder: Path,
        inputFile: String,
        outPutFile: String,
        progressFunction: (Progress) -> Unit
    ) {
        val ffmpeg = FFmpeg(ffmFolder.toFile().absolutePath + "/ffmpeg")
        val ffprobe = FFprobe(ffmFolder.toFile().absolutePath + "/ffprobe")

        val builder = FFmpegBuilder()
            .setInput(inputFile)
            .overrideOutputFiles(true)
            .addOutput(outPutFile)
            .done()

        val probeResult = ffprobe.probe(inputFile)
        val executor = FFmpegExecutor(ffmpeg, ffprobe)
        val durationNs: Double = probeResult.getFormat().duration * TimeUnit.SECONDS.toNanos(1)
        executor.createJob(builder, ProgressListener { progress ->
            val progressValue = ((progress.out_time_ns / durationNs) * 100).toInt()
            val outPutFileName = Paths.get(outPutFile).toFile().name
            progressFunction.invoke(Progress(progressValue, "Generating $outPutFileName..."))
        }).run()
    }

    private fun downloadFileFromUrl(folder: Path, url: String, progressFunction: (Progress) -> Unit): Path {
        val downloadFolder = folder.resolve("download")

        if (Files.exists(downloadFolder)) {
            FileUtils.deleteDirectory(downloadFolder.toFile())
        }
        Files.createDirectories(downloadFolder)
        val downloadFile = downloadFolder.resolve("download.zip")

        val urlNet = URL(url)
        val urlConnection = urlNet.openConnection()
        urlConnection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
        )

        urlConnection.connect()
        val contentLength = urlConnection.contentLength.toLong()
        urlConnection.getInputStream().use { input ->
            ProgressFileOutStream(downloadFile.toFile(), contentLength) { progress ->
                progressFunction.invoke(Progress((progress * 100).toInt(), "Downloading ffmpeg..."))
            }.use { output ->
                IOUtils.copy(input, output)
            }
        }
        val extractAbleFile = ZipFile(downloadFile.toFile())
        extractAbleFile.extractAll(downloadFolder.toFile().absolutePath)
        val downloadedFile = downloadFolder.toFile().listFiles { f -> f.name != "download.zip" }!!.first()
        return downloadedFile.toPath()
    }

    class ProgressFileOutStream(file: File, val fileSize: Long, val progress: (Double) -> Unit) :
        FileOutputStream(file) {
        var currentWrittenBytes: Long = 0
        var lastTimeWritten: Long = 0

        /**
         * Writes the specified byte to this file output stream. Implements
         * the `write` method of `OutputStream`.
         *
         * @param      b   the byte to be written.
         * @exception  IOException  if an I/O error occurs.
         */
        override fun write(b: Int) {
            super.write(b)
            currentWrittenBytes++
            updateProgress()
        }

        /**
         * Writes `b.length` bytes from the specified byte array
         * to this file output stream.
         *
         * @param      b   the data.
         * @exception  IOException  if an I/O error occurs.
         */
        override fun write(b: ByteArray) {
            super.write(b)
            currentWrittenBytes += b.size
            updateProgress()
        }

        /**
         * Writes `len` bytes from the specified byte array
         * starting at offset `off` to this file output stream.
         *
         * @param      b     the data.
         * @param      off   the start offset in the data.
         * @param      len   the number of bytes to write.
         * @exception  IOException  if an I/O error occurs.
         */
        override fun write(b: ByteArray, off: Int, len: Int) {
            super.write(b, off, len)
            currentWrittenBytes += len
            updateProgress()
        }

        /**
         * Updates the progress observers.
         */
        private fun updateProgress() {
            if (currentWrittenBytes - lastTimeWritten > 100000) {
                lastTimeWritten = currentWrittenBytes
                progress.invoke((currentWrittenBytes.toDouble() / fileSize.toDouble()))
            }
        }
    }
}