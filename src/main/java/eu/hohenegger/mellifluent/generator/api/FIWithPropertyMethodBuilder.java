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


import eu.hohenegger.mellifluent.generator.model.IWithPropertyMethodBuilder;
import eu.hohenegger.mellifluent.generator.model.Util;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;


public class FIWithPropertyMethodBuilder extends AbstractFluentBuilder implements IWithPropertyMethodBuilder {
    public CtMethod<Object> build() {
        CtMethod<Object> withPropertyMethod = getTypeFactory().createMethod();
        withPropertyMethod.addModifier(ModifierKind.PUBLIC);
        withPropertyMethod.setSimpleName(("with" + (Util.capitalizeFirstLetter(getPropertyField().getSimpleName()))));
        CtParameter<Object> withParameter = getTypeFactory().createParameter();
        withParameter.setSimpleName(getPropertyField().getSimpleName());
        CtTypeReference<Object> withParameterRef = getTypeFactory().createTypeReference();
        withParameterRef.setSimpleName(getPropertyField().getType().getSimpleName());
        withParameter.setType(withParameterRef);
        withPropertyMethod.addParameter(withParameter);
        withPropertyMethod.setType(getBuilder().getReference());
        CtAssignment<Object, Object> ctAssignment = getTypeFactory().createAssignment();
        CtVariableRead<Object> parameterRead = getTypeFactory().createVariableRead();
        CtLocalVariableReference<Object> parameterReference = getTypeFactory().createLocalVariableReference();
        parameterReference.setSimpleName(getPropertyName());
        parameterRead.setVariable(parameterReference);
        ctAssignment.setAssigned(getFieldWrite());
        ctAssignment.setAssignment(parameterRead);
        CtBlock<Object> block = getTypeFactory().createBlock();
        block.addStatement(ctAssignment);
        CtInvocation<Object> selfInvocation = getTypeFactory().createInvocation();
        CtMethod<Object> selfMethod = ((CtMethod<Object>) (getAbstractBuilder().getMethodsByName("self").get(0)));
        selfMethod.setSimpleName("self");
        selfInvocation.setExecutable(selfMethod.getReference());
        CtReturn<Object> withMethodReturn = getTypeFactory().createReturn();
        withMethodReturn.setReturnedExpression(selfInvocation);
        block.addStatement(withMethodReturn);
        withPropertyMethod.setBody(block);
        return withPropertyMethod;
    }

    @Override
    public CtType<?> getAbstractBuilder() {
        return AbstractBuilder;
    }

    @Override
    public CtType<Object> getBuilder() {
        return Builder;
    }

    @Override
    public CtVariableAccess<Object> getFieldWrite() {
        return FieldWrite;
    }

    @Override
    public String getPropertyName() {
        return PropertyName;
    }

    @Override
    public CtField<Object> getPropertyField() {
        return PropertyField;
    }

    @Override
    public Factory getTypeFactory() {
        return TypeFactory;
    }

    private CtType<?> AbstractBuilder;

    public FIWithPropertyMethodBuilder withAbstractBuilder(CtType AbstractBuilder) {
        this.AbstractBuilder = AbstractBuilder;
        return self();
    }

    private CtType<Object> Builder;

    public FIWithPropertyMethodBuilder withBuilder(CtType Builder) {
        this.Builder = Builder;
        return self();
    }

    private CtVariableAccess<Object> FieldWrite;

    public FIWithPropertyMethodBuilder withFieldWrite(CtVariableAccess FieldWrite) {
        this.FieldWrite = FieldWrite;
        return self();
    }

    private CtField<Object> PropertyField;

    public FIWithPropertyMethodBuilder withPropertyField(CtField PropertyField) {
        this.PropertyField = PropertyField;
        return self();
    }

    private String PropertyName;

    public FIWithPropertyMethodBuilder withPropertyName(String PropertyName) {
        this.PropertyName = PropertyName;
        return self();
    }

    private Factory TypeFactory;

    public FIWithPropertyMethodBuilder withTypeFactory(Factory TypeFactory) {
        this.TypeFactory = TypeFactory;
        return self();
    }

    @Override
    protected FIWithPropertyMethodBuilder self() {
        return this;
    }
}

