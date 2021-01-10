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


import eu.hohenegger.mellifluent.generator.model.IInstantiationBuilder;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;


public class FIInstantiationBuilder extends AbstractFluentBuilder implements IInstantiationBuilder {
    public CtAssignment<Object, Object> build() {
        CtAssignment<Object, Object> assignment = getTypeFactory().createAssignment();
        CtVariableWrite<Object> left = getTypeFactory().createVariableWrite();
        left.setType(((getTypeReference())));
        left.setVariable(getTypeFactory().createLocalVariableReference(getTypeReference(), getName()));
        assignment.setType(getTypeReference());
        CtNewClass<Object> right = getTypeFactory().createNewClass();
        right.setType(getTypeReference());
        assignment.setAssigned(left);
        assignment.setAssignment(right);
        return assignment;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public CtTypeReference<Object> getTypeReference() {
        return TypeReference;
    }

    @Override
    public Factory getTypeFactory() {
        return TypeFactory;
    }

    private String Name;

    public FIInstantiationBuilder withName(String Name) {
        this.Name = Name;
        return self();
    }

    private Factory TypeFactory;

    public FIInstantiationBuilder withTypeFactory(Factory TypeFactory) {
        this.TypeFactory = TypeFactory;
        return self();
    }

    private CtTypeReference<Object> TypeReference;

    public FIInstantiationBuilder withTypeReference(CtTypeReference TypeReference) {
        this.TypeReference = TypeReference;
        return self();
    }

    @Override
    protected FIInstantiationBuilder self() {
        return this;
    }
}

