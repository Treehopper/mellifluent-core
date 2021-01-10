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


import eu.hohenegger.mellifluent.generator.model.IFieldWriteBuilder;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;


public class FIFieldWriteBuilder extends AbstractFluentBuilder implements IFieldWriteBuilder {
    public CtVariableAccess<Object> build() {
        CtFieldReference<Object> fieldReference = getTypeFactory().createFieldReference();
        fieldReference.setSimpleName(getFieldName());
        return getTypeFactory().createVariableRead(fieldReference, false);
    }

    @Override
    public String getFieldName() {
        return FieldName;
    }

    @Override
    public Factory getTypeFactory() {
        return TypeFactory;
    }

    private String FieldName;

    public FIFieldWriteBuilder withFieldName(String FieldName) {
        this.FieldName = FieldName;
        return self();
    }

    private Factory TypeFactory;

    public FIFieldWriteBuilder withTypeFactory(Factory TypeFactory) {
        this.TypeFactory = TypeFactory;
        return self();
    }

    @Override
    protected FIFieldWriteBuilder self() {
        return this;
    }
}

