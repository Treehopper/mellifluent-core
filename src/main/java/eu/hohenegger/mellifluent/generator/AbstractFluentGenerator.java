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

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import eu.hohenegger.mellifluent.generator.model.Util;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.support.compiler.SpoonProgress;

public abstract class AbstractFluentGenerator<T extends Class> {

    public static final String GENERATED_BY = "GENERATED_BY";

    protected Launcher parsingLauncher;
    protected CtModel model;
    protected Factory typeFactory;

    private Launcher generatorLauncher;

    public void setup(Path root, ClassLoader classLoader, List<String> classPath, Consumer<CharSequence> progressListener) {
        parsingLauncher = new Launcher();

        parsingLauncher.getModelBuilder().addInputSource(root.toFile());
        parsingLauncher.getEnvironment().setCommentEnabled(false);
        parsingLauncher.getEnvironment().setNoClasspath(false);
        parsingLauncher.getEnvironment().setInputClassLoader(classLoader);

        if (classPath != null) {
            parsingLauncher.getEnvironment().setSourceClasspath(classPath.toArray(new String[classPath.size()]));
        }

        if (progressListener != null) {
            parsingLauncher.getEnvironment().setSpoonProgress(new SpoonProgress() {

                @Override
                public void start(Process process) { }

                @Override
                public void step(Process process, String task, int taskId, int nbTask) {
                    progressListener.accept(process + ": " + task);
                }

                @Override
                public void step(Process process, String task) { }

                @Override
                public void end(Process process) { }
            });
        }

        parsingLauncher.buildModel();

        generatorLauncher = new Launcher();
        model = generatorLauncher.getModel();
    }

    abstract CtPackage getGeneratedPackage();

    abstract protected void preRewrite(String packageName);

    abstract protected void postRewrite();

    private List<CtType<Object>> rewrite(List<CtType<T>> buildables) {
        return buildables.stream().map(this::rewriteClass).collect(toList());
    }

    abstract protected Filter<CtType<?>> createFilter(String packageName);

    public final List<CtType<Object>> generate(String packageName) throws GeneratorException {
        typeFactory = new Launcher().getFactory();
        preRewrite(packageName);
        Filter<CtType<?>> classFilter = createFilter(packageName);
        List<CtType<T>> buildables = Util.findClasses(classFilter, parsingLauncher.getModel());
        List<CtType<Object>> result = rewrite(buildables);
        postRewrite();
        return result;
    }

    abstract protected CtType<Object> rewriteClass(CtType<T> buildable);

    public Launcher getLauncher() {
        return generatorLauncher;
    }

}