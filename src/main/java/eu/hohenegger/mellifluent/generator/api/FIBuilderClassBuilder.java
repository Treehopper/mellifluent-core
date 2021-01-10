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


import eu.hohenegger.mellifluent.generator.model.IBuilderClassBuilder;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;


public class FIBuilderClassBuilder extends AbstractFluentBuilder implements IBuilderClassBuilder {
    public CtType<Object> build() {
        CtType<Object> builderClass = getTypeFactory().createClass(getBuilderName());
        builderClass.setSuperclass(getAbstractBuilderReference());
        builderClass.addSuperInterface(getBuildableReference());
        return builderClass;
    }

    @Override
    public CtTypeReference getBuildableReference() {
        return BuildableReference;
    }

    @Override
    public CtTypeReference getAbstractBuilderReference() {
        return AbstractBuilderReference;
    }

    @Override
    public String getBuilderName() {
        return BuilderName;
    }

    @Override
    public Factory getTypeFactory() {
        return TypeFactory;
    }

    private CtTypeReference AbstractBuilderReference;

    public FIBuilderClassBuilder withAbstractBuilderReference(CtTypeReference AbstractBuilderReference) {
        this.AbstractBuilderReference = AbstractBuilderReference;
        return self();
    }

    private CtTypeReference BuildableReference;

    public FIBuilderClassBuilder withBuildableReference(CtTypeReference BuildableReference) {
        this.BuildableReference = BuildableReference;
        return self();
    }

    private String BuilderName;

    public FIBuilderClassBuilder withBuilderName(String BuilderName) {
        this.BuilderName = BuilderName;
        return self();
    }

    private Factory TypeFactory;

    public FIBuilderClassBuilder withTypeFactory(Factory TypeFactory) {
        this.TypeFactory = TypeFactory;
        return self();
    }

    @Override
    protected FIBuilderClassBuilder self() {
        return this;
    }
}

