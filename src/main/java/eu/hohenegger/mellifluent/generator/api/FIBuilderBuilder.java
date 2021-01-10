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


import eu.hohenegger.mellifluent.generator.model.IBuilderBuilder;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;


public class FIBuilderBuilder extends AbstractFluentBuilder implements IBuilderBuilder {
    public CtType<Object> build() {
        CtType<Object> builderClass = getBuilderClass();
        builderClass.addMethod(getSelfOverrideMethod());
        builderClass.addMethod(getBuildMethod());
        builderClass.addModifier(ModifierKind.PUBLIC);
        builderClass.putMetadata(IBuilderBuilder.GENERATED_BY, getGeneratedByMetaData());
        return builderClass.clone();
    }

    @Override
    public CtType<Object> getBuilderClass() {
        return BuilderClass;
    }

    @Override
    public String getGeneratedByMetaData() {
        return GeneratedByMetaData;
    }

    @Override
    public CtMethod getBuildMethod() {
        return BuildMethod;
    }

    @Override
    public CtMethod getSelfOverrideMethod() {
        return SelfOverrideMethod;
    }

    private CtMethod BuildMethod;

    public FIBuilderBuilder withBuildMethod(CtMethod BuildMethod) {
        this.BuildMethod = BuildMethod;
        return self();
    }

    private CtType<Object> BuilderClass;

    public FIBuilderBuilder withBuilderClass(CtType BuilderClass) {
        this.BuilderClass = BuilderClass;
        return self();
    }

    private String GeneratedByMetaData;

    public FIBuilderBuilder withGeneratedByMetaData(String GeneratedByMetaData) {
        this.GeneratedByMetaData = GeneratedByMetaData;
        return self();
    }

    private CtMethod SelfOverrideMethod;

    public FIBuilderBuilder withSelfOverrideMethod(CtMethod SelfOverrideMethod) {
        this.SelfOverrideMethod = SelfOverrideMethod;
        return self();
    }

    @Override
    protected FIBuilderBuilder self() {
        return this;
    }
}

