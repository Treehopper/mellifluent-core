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

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Generated Fluent Spoon Model Test")
public class GeneratedUnassistedSpoonModelTest extends AbstractGeneratedSpoonModelTest {

    @BeforeAll
    public void setUp() {
        String srcPackageFolderName = "eu/hohenegger/mellifluent/generator/model/generics";
        Path srcPath = Paths.get("src/test/java");

        generator = new UnassistedFluentBuilderGenerator<>();

        Path folder = srcPath.resolve(srcPackageFolderName);
        generator.setup(folder, GeneratedSourceCompilationTest.class.getClassLoader(), null, null);

        try {
            sourcePackageName = srcPackageFolderName.replace('/', '.');
            generated = generator.generate(sourcePackageName);
        } catch (GeneratorException e) {
            fail(e);
        }
    }

}