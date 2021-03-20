/*-
 * #%L
 * mellifluent-core
 * %%
 * Copyright (C) 2021 Max Hohenegger <mellifluent@hohenegger.eu>
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

import java.io.File;
import java.util.List;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.reflect.declaration.CtPackageImpl;

public final class FileWriter extends Launcher {

    private boolean autoImports;

    private final class WriterFactory extends FactoryImpl {

        private static final long serialVersionUID = 1L;

        private WriterFactory() {
            super(new DefaultCoreFactory(), createEnvironment());
        }

        private void init(List<CtClass<?>> clazzes, String packageName, File sourceOutputDirectory) {
            CtPackage pack = createPackageHierarchy(packageName, getModel().getRootPackage());
            clazzes.stream().map(CtClass::clone).forEach(clazz -> {
                clazz.setFactory(this);
                pack.addType(clazz);
            });
            setSourceOutputDirectory(sourceOutputDirectory);
            getEnvironment().setAutoImports(autoImports);
            getEnvironment().setCommentEnabled(false);
        }

        private CtPackage createPackageHierarchy(String packageName, CtPackage parent) {
            String[] segments = packageName.split("\\.");
            CtPackage currentPackage = null;
            for (int i = 0; i < segments.length; i++) {
                currentPackage = new CtPackageImpl();
                currentPackage.setSimpleName(segments[i]);
                parent.addPackage(currentPackage);
                parent = currentPackage;
            }
            return currentPackage;
        }
    }

    public FileWriter(List<CtClass<?>> clonedClasses, String packageName, File sourceOutputDirectory, boolean autoImports) {
        this.autoImports = autoImports;
        WriterFactory writerFactory = (WriterFactory) getFactory();
        writerFactory.init(clonedClasses, packageName, sourceOutputDirectory);
    }

    @Override
    public Factory createFactory() {
        return new WriterFactory();
    }

    public File persist() {
        prettyprint();
        return getEnvironment().getOutputDestinationHandler()
                .getDefaultOutputDirectory();
    }
}
