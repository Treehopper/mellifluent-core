/*-
 * #%L
 * mellifluent-core
 * %%
 * Copyright (C) 2021 - 2021 Max Hohenegger <mellifluent@hohenegger.eu>
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static spoon.support.compiler.SpoonProgress.Process.MODEL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.hohenegger.mellifluent.generator.model.generics.ClassLevelGenerics;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.ModuleFactory.CtUnnamedModule;
import spoon.support.compiler.SpoonProgress;

@ExtendWith(MockitoExtension.class)
public class SpoonTest {

    private Launcher parsingLauncher;
    private ClassLoader classLoader;

    @Mock
    private SpoonProgress progress;

    @BeforeEach
    public void setUp() {
        parsingLauncher = new Launcher();

        Path root = Paths.get("src/test/java")
                .resolve(ClassLevelGenerics.class.getPackageName()
                .replace(".", "/"));

        classLoader = getClass().getClassLoader();

        parsingLauncher.getModelBuilder().addInputSource(root.toFile());
        Environment environment = parsingLauncher.getEnvironment();
        environment.setCommentEnabled(false);
        environment.setNoClasspath(false);
        environment.setInputClassLoader(classLoader);
        environment.setSpoonProgress(progress);
    }

    @Test
    public void testProcessing() throws Throwable {
        parsingLauncher.buildModel();

        verify(progress, atLeastOnce()).start(eq(MODEL));
        verify(progress, atLeastOnce()).end(eq(MODEL));
    }

    @Test
    public void testBuildModel() throws Throwable {
        parsingLauncher.buildModel();

        CtModel model = parsingLauncher.getModel();
        List<CtUnnamedModule> modules = model.filterChildren(CtUnnamedModule.class::isInstance).list();
        assertThat(modules).isNotNull();
        assertThat(modules).isNotEmpty();
        assertThat(modules).allSatisfy(element -> {
            assertThat(element).isInstanceOfAny(CtUnnamedModule.class);
        });

        List<CtPackage> packages = model.filterChildren(CtPackage.class::isInstance).list();
        assertThat(packages).isNotNull();
        assertThat(packages).isNotEmpty();
        assertThat(packages).allSatisfy(element -> {
            assertThat(element).isInstanceOfAny(CtPackage.class);
        });
    }

    @Test
    public void testClasses() throws Throwable {
        parsingLauncher.buildModel();

        CtModel model = parsingLauncher.getModel();
        List<CtClass<?>> classes = model.filterChildren(CtClass.class::isInstance).list();
        assertThat(classes).isNotNull();
        assertThat(classes).isNotEmpty();
        assertThat(classes).allSatisfy(clazz -> {
            assertThat(clazz).isInstanceOfAny(CtClass.class);
        });
    }

    @Test
    @DisplayName("Package hierarchy behavior with Spoon 7.5.0")
    public void testCopyModel(@TempDir Path tempDir) throws Throwable {
        parsingLauncher.buildModel();

        CtModel model = parsingLauncher.getModel();
        List<CtClass<?>> classes = model.filterChildren(CtClass.class::isInstance).list();

        FileWriter writer = new FileWriter(classes, "foobar", tempDir.toFile(), true);
        writer.persist();
        assertThat(Files.list(tempDir.resolve("foobar"))).isNotEmpty();
    }
    
    @Test
    @DisplayName("Package hierarchy behavior with Spoon 7.6.x")
    public void testCopyModelNestedPackage(@TempDir Path tempDir) throws Throwable {
        parsingLauncher.buildModel();

        CtModel model = parsingLauncher.getModel();
        List<CtClass<?>> classes = model.filterChildren(CtClass.class::isInstance).list();

        FileWriter writer = new FileWriter(classes, "foo.bar", tempDir.toFile(), true);
        writer.persist();
        assertThat(Files.list(tempDir.resolve("foo").resolve("bar"))).isNotEmpty();
    }

}