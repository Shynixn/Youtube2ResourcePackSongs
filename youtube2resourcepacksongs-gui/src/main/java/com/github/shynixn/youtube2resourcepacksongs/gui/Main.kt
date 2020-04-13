@file:Suppress("DuplicatedCode", "UNNECESSARY_SAFE_CALL", "CAST_NEVER_SUCCEEDS")

package com.github.shynixn.youtube2resourcepacksongs.gui

import com.github.shynixn.youtube2resourcepacksongs.api.Youtube2ResourcePackSongsApi
import com.github.shynixn.youtube2resourcepacksongs.gui.entity.Configuration
import com.github.shynixn.youtube2resourcepacksongs.gui.impl.Logger
import com.github.shynixn.youtube2resourcepacksongs.gui.swing.GuiFrame
import org.apache.commons.cli.*
import java.io.FileNotFoundException
import java.nio.file.Files
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
private val configuration = Configuration()

fun main(args: Array<String>) {
    val options = Options()
    options.addOption(Option("h", "help", false, "Print help."))
    options.addOption(Option("hl", "headless", false, "Runs the program headless."))
    options.addOption(Option("ifile", "inputfile", true, "Input csv file path"))
    options.addOption(Option("ofile", "outputfile", true, "Output resourcepack zip file path."))
    options.addOption(Option("i", "immediately", false, "Runs the program immediately."))

    val parser: CommandLineParser = DefaultParser()

    val cmd = try {
        parser.parse(options, args)
    } catch (e: Exception) {
        printHelp(options)
        throw IllegalArgumentException(e.message)
    }

    if (cmd.hasOption("h")) {
        printHelp(options)
        return
    }

    if (cmd.hasOption("ifile")) {
        val inputFile = cmd.getOptionValue("ifile")
        configuration.inputFilePath = try {
            val filePath = Paths.get(inputFile)
            if (!Files.exists(filePath)) {
                throw FileNotFoundException("File $inputFile does not exist!")
            }
            filePath
        } catch (e: Exception) {
            printHelp(options)
            throw IllegalArgumentException(e.message)
        }
    }

    if (cmd.hasOption("ofile")) {
        val outPutFile = cmd.getOptionValue("ofile")
        configuration.outputFilePath = try {
            val filePath = Paths.get(outPutFile)
            if (!Files.exists(filePath)) {
                throw FileNotFoundException("File $outPutFile does not exist!")
            }
            filePath
        } catch (e: Exception) {
            printHelp(options)
            throw IllegalArgumentException(e.message)
        }
    }

    val gui = if (!cmd.hasOption("hl")) {
        GuiFrame.showGUI(configuration)
    } else {
        null
    }

    if (cmd.hasOption("i")) {
        gui?.buttonStart!!.isEnabled = false
        Youtube2ResourcePackSongsApi.convertAsync(
            configuration.inputFilePath.toFile(),
            configuration.outputFilePath.toFile()
        ) { progress ->
            gui?.updateProgress(progress)
        }.exceptionally { e ->
            gui?.setProgressMessage("Error. See latest.log for details.")
            Logger.error(e)
            null
        }.thenAccept {
            gui?.buttonStart!!.isEnabled = true
        }
    }
}

/**
 * Prints the help page.
 */
private fun printHelp(options: Options) {
    val formatter = HelpFormatter()
    formatter.printHelp("youtube2resourcepacksongs", options)
}