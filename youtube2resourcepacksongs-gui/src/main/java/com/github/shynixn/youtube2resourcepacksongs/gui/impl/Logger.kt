package com.github.shynixn.youtube2resourcepacksongs.gui.impl

import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.SimpleFormatter

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
object Logger {
    private val logger = java.util.logging.Logger.getLogger("Youtube2ResourcePack")

    /**
     * Init logger.
     */
    init {
        try {
            Files.deleteIfExists(Paths.get("latest.log"))
            val fh = FileHandler("latest.log")
            logger.addHandler(fh)
            val formatter = SimpleFormatter()
            fh.formatter = formatter
        } catch (e: Exception) {
            println("Cannot initialize logger.")
        }
    }

    /**
     * Log throwable.
     */
    fun error(e: Throwable) {
        logger.log(Level.SEVERE, "Error", e)
    }

    /**
     * Log info.
     */
    fun info(message: String) {
        logger.log(Level.INFO, message);
    }
}