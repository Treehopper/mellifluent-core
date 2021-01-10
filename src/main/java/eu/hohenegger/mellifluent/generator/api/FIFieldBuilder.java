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


import eu.hohenegger.mellifluent.generator.model.IFieldBuilder;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;


public class FIFieldBuilder extends AbstractFluentBuilder implements IFieldBuilder {
    public CtField<Object> build() {
        CtField<Object> ctField = getTypeFactory().createField();
        ctField.addModifier(ModifierKind.PRIVATE);
        ctField.setType(getGetterDeclaringType());
        ctField.setSimpleName(getSimpleName());
        return ctField;
    }

    @Override
    public CtTypeReference getGetterDeclaringType() {
        return GetterDeclaringType;
    }

    @Override
    public String getSimpleName() {
        return SimpleName;
    }

    @Override
    public Factory getTypeFactory() {
        return TypeFactory;
    }

    private CtTypeReference GetterDeclaringType;

    public FIFieldBuilder withGetterDeclaringType(CtTypeReference GetterDeclaringType) {
        this.GetterDeclaringType = GetterDeclaringType;
        return self();
    }

    private String SimpleName;

    public FIFieldBuilder withSimpleName(String SimpleName) {
        this.SimpleName = SimpleName;
        return self();
    }

    private Factory TypeFactory;

    public FIFieldBuilder withTypeFactory(Factory TypeFactory) {
        this.TypeFactory = TypeFactory;
        return self();
    }

    @Override
    protected FIFieldBuilder self() {
        return this;
    }
}

