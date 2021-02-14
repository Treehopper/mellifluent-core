/*-
 * #%L
 * mellifluent-core
 * %%
 * Copyright (C) 2020 - 2021 Max Hohenegger <mellifluent@hohenegger.eu>
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.hohenegger.mellifluent.generator;

import static java.io.File.pathSeparator;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AbstractSourceCompilationTest {

    public static final Path TARGET_PATH = Paths.get("target/generated-sources/java");
    public static final String TARGET_SUB_PATH = "foo";
    public static final String TARGET_PACKAGE_FOLDER_NAME = TARGET_SUB_PATH + ".bar";


    public String sourcePackageName;
    public String targetPackageName;
    public Path folder;

    public AbstractFluentGenerator<Class<?>> generator;

    @Test
    @DisplayName("Verify that target contain expected folder")
    public void testTargetFolderStructure() throws Throwable {
        generator.generate(sourcePackageName);
        File outputDir = new FileWriter(generator, TARGET_PATH.toString(), targetPackageName, true).persist();

        assertThat(outputDir.list()).contains(TARGET_SUB_PATH);
    }

    @AfterAll
    public void tearDown() throws IOException {
        deleteRecursive(TARGET_PATH.resolve(TARGET_SUB_PATH));
        assertFalse(Files.exists(TARGET_PATH.resolve(TARGET_SUB_PATH)), "Directory still exists");
    }

    protected void deleteRecursive(Path generatedDirectory) throws IOException {
        Files.walk(generatedDirectory)
                .sorted(reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    protected URL toUrl(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e1) {
            fail(e1);
            return null;
        }
    }

    protected List<File> getClassPathEntries() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(pathSeparator);
        return Stream.of(classpathEntries).map(entry -> new File(entry)).collect(toList());
    }

}