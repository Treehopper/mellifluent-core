/*-
 * #%L
 * mellifluent-core
 * %%
 * Copyright (C) 2020 - 2022 Max Hohenegger <mellifluent@hohenegger.eu>
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

import eu.hohenegger.mellifluent.generator.api.FIAbstractFluentBuilder;
import eu.hohenegger.mellifluent.generator.api.FIPackageBuilder;
import eu.hohenegger.mellifluent.generator.model.Util;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.support.compiler.SpoonProgress;

public abstract class AbstractFluentGenerator<T extends Class> {

  private final class SpoonDebugProgress implements SpoonProgress {
    private final Consumer<CharSequence> progressListener;

    private SpoonDebugProgress(Consumer<CharSequence> progressListener) {
      this.progressListener = progressListener;
    }

    @Override
    public void start(Process process) {}

    @Override
    public void step(Process process, String task, int taskId, int nbTask) {
      progressListener.accept(process + ": " + task);
    }

    @Override
    public void step(Process process, String task) {}

    @Override
    public void end(Process process) {}
  }

  public static final String GENERATED_BY = "GENERATED_BY";

  protected Launcher parsingLauncher;
  protected CtModel model;
  protected Factory typeFactory;
  protected CtPackage builderPackage;
  protected CtClass<?> abstractBuilder;

  private Launcher generatorLauncher;
  private Consumer<CharSequence> progressListener;

  public void setup(
      Path root,
      ClassLoader classLoader,
      List<String> classPath,
      Consumer<CharSequence> progressListener) {
    if (progressListener == null) {
      progressListener =
          (chars) -> {
            // null progress listener
          };
    }
    this.progressListener = progressListener;
    parsingLauncher = new Launcher();

    parsingLauncher.getModelBuilder().addInputSource(root.toFile());
    parsingLauncher.getEnvironment().setCommentEnabled(false);
    parsingLauncher.getEnvironment().setNoClasspath(false);
    parsingLauncher.getEnvironment().setInputClassLoader(classLoader);

    if (classPath != null) {
      parsingLauncher
          .getEnvironment()
          .setSourceClasspath(classPath.toArray(new String[classPath.size()]));
    }

    parsingLauncher.getEnvironment().setSpoonProgress(new SpoonDebugProgress(progressListener));

    parsingLauncher.buildModel();

    generatorLauncher = new Launcher();
    model = generatorLauncher.getModel();
  }

  public void setup(List<File> jarFiles, Consumer<CharSequence> progressListener) {
    if (progressListener == null) {
      progressListener =
          (chars) -> {
            // null progress listener
          };
    }
    this.progressListener = progressListener;
    parsingLauncher = new Launcher();

    jarFiles.forEach(
        file -> {
          parsingLauncher.getModelBuilder().addInputSource(file);
        });

    parsingLauncher.getEnvironment().setCommentEnabled(false);

    // avoid failures when some (unused) dependencies are not resolved
    parsingLauncher.getEnvironment().setNoClasspath(true);
    //        parsingLauncher.getEnvironment().setShouldCompile(false);

    parsingLauncher.getEnvironment().setSpoonProgress(new SpoonDebugProgress(progressListener));

    parsingLauncher.buildModel();

    generatorLauncher = new Launcher();
    model = generatorLauncher.getModel();
  }

  abstract CtPackage getGeneratedPackage();

  protected void preRewrite(String packageName) {
    abstractBuilder = new FIAbstractFluentBuilder().withTypeFactory(typeFactory).build();
    builderPackage =
        new FIPackageBuilder().withTypeFactory(typeFactory).withPackageName(packageName).build();
  }

  protected abstract void postRewrite();

  private List<CtClass<?>> rewrite(List<CtType<T>> buildables, boolean includeInheritedMethods) {
    List<CtClass<?>> result = new ArrayList<>();
    buildables.forEach(
        buildable -> {
          try {
            result.add(rewriteClass(buildable, includeInheritedMethods));
          } catch (Exception e) {
            throw new RuntimeException("Error while rewriting: " + buildable.getSimpleName(), e);
          }
        });
    return result;
  }

  protected abstract Filter<CtType<?>> createFilter(
      String packageName, Consumer<CharSequence> progressListener);

  public final List<CtClass<?>> generate(String packageName) {
    return generate(packageName, false);
  }

  public final List<CtClass<?>> generate(String packageName, boolean includeInheritedMethods) {
    typeFactory = new Launcher().getFactory();

    progressListener.accept("Writing to package: " + packageName);
    preRewrite(packageName);
    Filter<CtType<?>> classFilter = createFilter(packageName, progressListener);
    List<CtType<T>> buildables = Util.findClasses(classFilter, parsingLauncher.getModel());
    if (buildables.isEmpty()) {
      throw new IllegalArgumentException("No buildable classes found in " + packageName);
    }
    progressListener.accept(
        "Found buildable classes: "
            + buildables.stream().map(CtType::getSimpleName).collect(Collectors.joining(",")));
    List<CtClass<?>> result = rewrite(buildables, includeInheritedMethods);
    progressListener.accept(
        "Writing: " + result.stream().map(CtType::getSimpleName).collect(Collectors.joining(",")));

    postRewrite();
    return result;
  }

  protected abstract CtClass<?> rewriteClass(CtType<T> buildable, boolean includeInheritedMethods);

  public CtClass<?> getAbstractBuilder() {
    return abstractBuilder;
  }
}
