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

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Generated Fluent Source Compilation Test")
public class GeneratedUnassistedSourceCompilationTest extends AbstractSourceCompilationTest {

    @BeforeAll
    protected void setUpAll() {
        String SRC_PACKAGE_FOLDER_NAME = "eu/hohenegger/mellifluent/generator/model/generics";
        folder = Paths.get("src/test/java")
                .resolve(SRC_PACKAGE_FOLDER_NAME);
        sourcePackageName = SRC_PACKAGE_FOLDER_NAME.replace('/', '.');
        targetPackageName = TARGET_PACKAGE_FOLDER_NAME.replace('/', '.');

        generator = new UnassistedFluentBuilderGenerator<>();
        generator.setup(folder, getClass().getClassLoader(), null, null);
    }

}