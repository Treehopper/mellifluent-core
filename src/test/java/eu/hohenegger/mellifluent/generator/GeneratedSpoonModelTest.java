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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.reflect.declaration.ModifierKind.PUBLIC;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Generated Assisted Spoon Model Test")
public class GeneratedSpoonModelTest extends AbstractGeneratedSpoonModelTest {

    @BeforeAll
    public void setUp() {
        String srcPackageFolderName = "eu/hohenegger/mellifluent/generator/model";
        Path srcPath = Paths.get("src/main/java");

        generator = new FluentBuilderGenerator<>();

        Path folder = srcPath.resolve(srcPackageFolderName);
        generator.setup(folder, GeneratedSourceCompilationTest.class.getClassLoader(), null, null);

        try {
            sourcePackageName = srcPackageFolderName.replace('/', '.');
            generated = generator.generate(sourcePackageName);
        } catch (GeneratorException e) {
            fail(e);
        }
    }

    @DisplayName("Verify Get Methods for")
    @MethodSource("ctTypes")
    @ParameterizedTest(name = "{displayName}: {arguments}")
    public void testGetMethods(CtType<Object> ctType) throws Throwable {

        List<CtMethod<?>> getMethods = ctType.getMethods().stream()
                .filter(method -> method.getSimpleName().startsWith("get")).collect(toList());
        assertThat(getMethods).isNotEmpty();
        assertThat(getMethods).allSatisfy(method -> {
            assertThat(method.getAnnotation(Override.class)).isNotNull();
            assertThat(method.getVisibility()).isEqualTo(PUBLIC);
        });
    }

}