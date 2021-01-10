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

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static java.io.File.pathSeparator;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Generated Source Compilation Test")
public class GeneratedSourceCompilationTest {

    private static final Path SRC_PATH = Paths.get("src/main/java");
    private static final Path TARGET_PATH = Paths.get("target/generated-sources/java");

    private static final String SRC_PACKAGE_FOLDER_NAME = "eu/hohenegger/mellifluent/generator/model";
    private static final String TARGET_SUB_PATH = "foo";
    private static final String TARGET_PACKAGE_FOLDER_NAME = TARGET_SUB_PATH + ".bar";


    private FluentBuilderGenerator<Class<?>> generator;

    private String sourcePackageName;
    private String targetPackageName;

    @BeforeAll
    public void setUp() {
        generator = new FluentBuilderGenerator<>();

        Path folder = SRC_PATH.resolve(SRC_PACKAGE_FOLDER_NAME);
        generator.setup(folder, getClass().getClassLoader(), null, null);
        sourcePackageName = SRC_PACKAGE_FOLDER_NAME.replace('/', '.');
        targetPackageName = TARGET_PACKAGE_FOLDER_NAME.replace('/', '.');
    }

    @Test
    @DisplayName("Verify that generated source files can compiled")
    public void testCompilation() throws Throwable {
        generator.generate(sourcePackageName);
        File outputDir = new FileWriter(generator, TARGET_PATH.toString(), targetPackageName, true).persist();

        assertThat(outputDir.list()).contains(TARGET_SUB_PATH);
        File subDir = new File(outputDir, TARGET_SUB_PATH);

        List<JavaFileObject> sources = Files.walk(subDir.toPath())
                .filter(Files::isRegularFile)
                .map(Path::toUri)
                .map(this::toUrl)
                .map(JavaFileObjects::forResource)
                .collect(toList());

        Compilation compilation = javac()
                .withClasspath(getClassPathEntries())
                .compile(sources);
        assertThat(compilation).succeeded();
        assertThat(compilation.diagnostics()).isNotEmpty();
        assertThat(compilation.diagnostics()).allSatisfy(note -> {
            assertThat(note.getKind()).isEqualTo(NOTE);
        });

        assertTrue(Files.isDirectory(subDir.toPath()));
    }

    @Test
    @DisplayName("Verify that target contain expected folder")
    public void testTargetFolderStructure() throws Throwable {
        generator.generate(sourcePackageName);
        File outputDir = new FileWriter(generator, TARGET_PATH.toString(), targetPackageName, true).persist();

        assertThat(outputDir.list()).contains(TARGET_SUB_PATH);
    }

    @AfterAll
    public void tearDown() throws IOException {
        //		deleteRecursive(TARGET_PATH.resolve(TARGET_SUB_PATH));
        //
        //		assertFalse("Directory still exists", Files.exists(TARGET_PATH.resolve(TARGET_SUB_PATH)));
    }

    private void deleteRecursive(Path generatedDirectory) throws IOException {
        Files.walk(generatedDirectory)
        .sorted(reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
    }

    private URL toUrl(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e1) {
            fail(e1);
            return null;
        }
    }

    private List<File> getClassPathEntries() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(pathSeparator);
        return Stream.of(classpathEntries).map(entry -> new File(entry)).collect(toList());
    }

}