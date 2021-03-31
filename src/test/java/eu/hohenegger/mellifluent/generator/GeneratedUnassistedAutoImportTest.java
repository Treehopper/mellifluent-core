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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import eu.hohenegger.mellifluent.generator.model.imports.FileReferenceClass;
import spoon.reflect.declaration.CtClass;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeneratedUnassistedAutoImportTest {

    public static final Path TARGET_PATH = Paths.get("target/generated-sources/java");
    public static final String TARGET_SUB_PATH = "foo";
    public static final String TARGET_PACKAGE_FOLDER_NAME = TARGET_SUB_PATH + ".bar";

    public String sourcePackageName;
    public String targetPackageName;
    public Path folder;

    public AbstractFluentGenerator<Class<?>> generator;


    @BeforeAll
    protected void setUpAll() {
        folder = Paths.get("src/test/java")
                .resolve(FileReferenceClass.class.getPackageName()
                        .replace('.', '/'));
        sourcePackageName = FileReferenceClass.class.getPackageName();
        targetPackageName = TARGET_PACKAGE_FOLDER_NAME.replace('/', '.');

        generator = new UnassistedFluentBuilderGenerator<>();
        generator.setup(folder, getClass().getClassLoader(), null, null);
    }

    @TempDir
    Path tempDir;

    @ParameterizedTest(name = "{index} When autoimport = {0} then output must contain {1}")
    @CsvSource({
                    "true,import java.io.File",
                    "false,private java.io.File"
               })
    @DisplayName("Verify the output contains the right imports")
    public void testFileWriter(boolean autoImport, String fileReference) throws Throwable {
        folder = Paths.get("src/test/java")
                .resolve(FileReferenceClass.class.getPackageName()
                        .replace('.', '/'));
        sourcePackageName = FileReferenceClass.class.getPackageName();
        targetPackageName = TARGET_PACKAGE_FOLDER_NAME.replace('/', '.');

        List<CtClass<?>> list = generator.generate(sourcePackageName);

        new FileWriter(list, targetPackageName, tempDir.toFile(), autoImport).persist();

        Files.walk(tempDir)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .findAny()
                .ifPresentOrElse(file -> {
                    assertThat(contentOf(file)).contains(fileReference);
                }, Assertions::fail);
    }
}