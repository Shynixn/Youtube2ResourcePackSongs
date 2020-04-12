@file:Suppress("DuplicatedCode", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package com.github.shynixn.youtube2resourcepacksongs.api

import com.github.shynixn.youtube2resourcepacksongs.api.entity.Progress
import com.github.shynixn.youtube2resourcepacksongs.api.entity.Video
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlinx.coroutines.*
import java.nio.file.Path
import kotlin.reflect.KFunction

/**
 * API to convert videos to resource packs.
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
object Youtube2ResourcePackSongsApi {
    private val csvFileService =
        Class.forName("com.github.shynixn.youtube2resourcepacksongs.logic.service.CsvFileServiceImpl")
            .getDeclaredConstructor()
            .newInstance()

    private val resourcePackService =
        Class.forName("com.github.shynixn.youtube2resourcepacksongs.logic.service.ResourcePackServiceImpl")
            .getDeclaredConstructor()
            .newInstance()

    /**
     * Converts the given [inputFile] to a new resource pack at the given [outputFile].
     * Overrides the output file if it already exists. Blocks the calling thread until finished.
     * @param inputFile FilePath where the videos are listed in csv format.
     * @param outputFile FilePath where the resource pack gets generated. Overrides existing files.
     * @param progress progress CallBack on the same thread which gives info of the current progress.
     */
    fun convert(inputFile: File, outputFile: File, progress: (Progress) -> Unit) {
        progress.invoke(Progress(0, "Parsing input file..."))
        val videos = csvFileService.javaClass.getDeclaredMethod("parseFile", Path::class.java)
            .invoke(csvFileService, inputFile.toPath()) as Collection<Video>
        progress.invoke(Progress(0, "Finished parsing input file."))
        convert(videos, outputFile, progress)
    }

    /**
     * Converts the given [inputFile] to a new resource pack at the given [outputFile].
     * Overrides the output file if it already exists. Blocks the calling thread until finished.
     * @param inputFile FilePath where the videos are listed in csv format.
     * @param outputFile FilePath where the resource pack gets generated. Overrides existing files.
     * @param progress progress CallBack on the same thread which gives info of the current progress.
     * @return CompletionStage which completes with a null object when completed.
     */
    fun convertAsync(inputFile: File, outputFile: File, progress: (Progress) -> Unit): CompletionStage<Void?> {
        val completeAble = CompletableFuture<Void?>()

        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    convert(inputFile, outputFile, progress)
                }
                completeAble.complete(null)
            } catch (e: Exception) {
                completeAble.completeExceptionally(e)
            }
        }

        return completeAble
    }

    /**
     * Converts the given [videos] to a new resource pack at the given [outputFile].
     * Overrides the output file if it already exists. Blocks the calling thread until finished.
     * @param videos Collection of videos to be included in the resource pack.
     * @param outputFile FilePath where the resource pack gets generated. Overrides existing files.
     * @param progress progress CallBack on the same thread which gives info of the current progress.
     */
    fun convert(videos: Collection<Video>, outputFile: File, progress: (Progress) -> Unit) {
        progress.invoke(Progress(0, "Generating resource pack..."))
        resourcePackService.javaClass.getDeclaredMethod(
            "generateResourcePack",
            Collection::class.java,
            Path::class.java,
            Any::class.java
        )
            .invoke(resourcePackService, videos, outputFile.toPath(), progress)
        progress.invoke(Progress(100, "Finished generating resource pack."))
    }

    /**
     * Converts the given [videos] to a new resource pack at the given [outputFile].
     * Overrides the output file if it already exists. Runs asynchronously until it completes on the calling thread.
     * @param videos Collection of videos to be included in the resource pack.
     * @param outputFile FilePath where the resource pack gets generated. Overrides existing files.
     * @param progress progress CallBack on the same thread which gives info of the current progress.
     * @return CompletionStage which completes with a null object when completed.
     */
    fun convertAsync(
        videos: Collection<Video>,
        outputFile: File,
        progress: (Progress) -> Unit
    ): CompletionStage<Void?> {
        val completeAble = CompletableFuture<Void?>()

        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    convert(videos, outputFile, progress)
                }
                completeAble.complete(null)
            } catch (e: Exception) {
                completeAble.completeExceptionally(e)
            }
        }

        return completeAble
    }
}