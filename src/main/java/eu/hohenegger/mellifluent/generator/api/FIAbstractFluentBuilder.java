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


import eu.hohenegger.mellifluent.generator.model.IAbstractFluentBuilder;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;


public class FIAbstractFluentBuilder extends AbstractFluentBuilder implements IAbstractFluentBuilder {
    public CtType<?> build() {
        CtType<?> result = getTypeFactory().createClass("AbstractFluentBuilder");
        result.addModifier(ModifierKind.PUBLIC);
        result.addModifier(ModifierKind.ABSTRACT);
        CtMethod selfMethod = getTypeFactory().createMethod();
        selfMethod.setSimpleName("self");
        selfMethod.addModifier(ModifierKind.PROTECTED);
        selfMethod.addModifier(ModifierKind.ABSTRACT);
        selfMethod.setType(result.getReference());
        result.addMethod(selfMethod);
        return result;
    }

    @Override
    public Factory getTypeFactory() {
        return TypeFactory;
    }

    private Factory TypeFactory;

    public FIAbstractFluentBuilder withTypeFactory(Factory TypeFactory) {
        this.TypeFactory = TypeFactory;
        return self();
    }

    @Override
    protected FIAbstractFluentBuilder self() {
        return this;
    }
}

