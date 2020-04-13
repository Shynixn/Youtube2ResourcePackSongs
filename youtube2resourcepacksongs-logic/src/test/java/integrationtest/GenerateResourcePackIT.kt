package integrationtest

import com.github.shynixn.youtube2resourcepacksongs.api.Youtube2ResourcePackSongsApi
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Integrationtest.
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
class GenerateResourcePackIT {
    /**
     * Given
     *      a valid input.csv
     * When
     *     the resource pack gets generated
     * Then
     *     a resource pack containing the correct data should be generated.
     */
    @Test
    fun generateResourcePackTest() {
        // Arrange
        val inputFile = Paths.get("build/test/input.csv")
        val ouputFile = Paths.get("build/test/resourcepack.zip")
        Files.writeString(inputFile, "https://www.youtube.com/watch?v=dQw4w9WgXcQ,custom/demosong")
        val expectedZipFileSize = 3670000L

        // Act
        Youtube2ResourcePackSongsApi.convert(inputFile.toFile(), ouputFile.toFile()) { progress ->
            println(progress.message + ": " + progress.value + "%")
        }

        // Assert
        Assert.assertTrue(Files.size(ouputFile) > expectedZipFileSize)
    }

    @BeforeEach
    fun setupTestFolder() {
        val testFolder = Paths.get("build/test")
        if (Files.exists(testFolder)) {
            FileUtils.deleteDirectory(testFolder.toFile())
        }
        Files.createDirectories(testFolder)
    }
}