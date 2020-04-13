@file:Suppress("DuplicatedCode", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package com.github.shynixn.youtube2resourcepacksongs.api

import com.github.shynixn.youtube2resourcepacksongs.api.entity.Progress
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlinx.coroutines.*
import java.nio.file.Path

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
        resourcePackService.javaClass.getDeclaredMethod(
            "generateResourcePack",
            Path::class.java,
            Path::class.java,
            Any::class.java
        ).invoke(resourcePackService, inputFile.toPath(), outputFile.toPath(), progress)
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
            val result = withContext(Dispatchers.IO) {
                try {
                    convert(inputFile, outputFile, progress)
                    Unit
                } catch (e: Exception) {
                    e
                }
            }

            if (result is Throwable) {
                completeAble.completeExceptionally(result)
            } else {
                completeAble.complete(null)
            }
        }

        return completeAble
    }
}