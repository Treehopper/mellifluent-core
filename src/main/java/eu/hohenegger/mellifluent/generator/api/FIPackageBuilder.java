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
package eu.hohenegger.mellifluent.generator.api;


import eu.hohenegger.mellifluent.generator.model.IPackageBuilder;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;


public class FIPackageBuilder extends AbstractFluentBuilder implements IPackageBuilder {
    public CtPackage build() {
        CtPackage result = getTypeFactory().createPackage();
        result.setSimpleName(getPackageName());
        return result;
    }

    @Override
    public String getPackageName() {
        return PackageName;
    }

    @Override
    public Factory getTypeFactory() {
        return TypeFactory;
    }

    private String PackageName;

    public FIPackageBuilder withPackageName(String PackageName) {
        this.PackageName = PackageName;
        return self();
    }

    private Factory TypeFactory;

    public FIPackageBuilder withTypeFactory(Factory TypeFactory) {
        this.TypeFactory = TypeFactory;
        return self();
    }

    @Override
    protected FIPackageBuilder self() {
        return this;
    }
}

