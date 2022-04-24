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
package eu.hohenegger.mellifluent.generator.model;

import static spoon.reflect.declaration.ModifierKind.PUBLIC;

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
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;

public interface IWithPropertyMethodBuilder {

  default CtMethod<Object> build() {
    CtMethod<Object> withPropertyMethod = getTypeFactory().createMethod();
    withPropertyMethod.addModifier(PUBLIC);
    withPropertyMethod.setSimpleName(
        "with" + Util.capitalizeFirstLetter(getPropertyField().getSimpleName()));
    CtParameter<Object> withParameter = getTypeFactory().createParameter();
    withParameter.setSimpleName(getPropertyField().getSimpleName());
    CtTypeReference<Object> withParameterRef = getTypeFactory().createTypeReference();
    withParameterRef.setSimpleName(
        getPropertyField().getType().getSimpleName()); // TODO: why cant we use the type directly?
    withParameter.setType(withParameterRef);
    withPropertyMethod.addParameter(withParameter);
    withPropertyMethod.setType(getBuilder().getReference());

    CtAssignment<Object, Object> ctAssignment = getTypeFactory().createAssignment();
    CtVariableRead<Object> parameterRead = getTypeFactory().createVariableRead();
    CtLocalVariableReference<Object> parameterReference =
        getTypeFactory().createLocalVariableReference();
    parameterReference.setSimpleName(getPropertyName());
    parameterRead.setVariable(parameterReference);
    ctAssignment.setAssigned(getFieldWrite());
    ctAssignment.setAssignment(parameterRead);
    CtBlock<Object> block = getTypeFactory().createBlock();
    block.addStatement(ctAssignment);

    // self-call ===============================
    CtInvocation<Object> selfInvocation = getTypeFactory().createInvocation();
    CtMethod<Object> selfMethod =
        (CtMethod<Object>) getAbstractBuilder().getMethodsByName("self").get(0);
    selfMethod.setSimpleName("self");
    selfInvocation.setExecutable(selfMethod.getReference());
    CtReturn<Object> withMethodReturn = getTypeFactory().createReturn();
    withMethodReturn.setReturnedExpression(selfInvocation);
    block.addStatement(withMethodReturn);
    // ===============================

    withPropertyMethod.setBody(block);

    return withPropertyMethod;
  }

  CtType<?> getAbstractBuilder();

  CtType<Object> getBuilder();

  CtVariableAccess<Object> getFieldWrite();

  String getPropertyName();

  CtField<Object> getPropertyField();

  Factory getTypeFactory();
}
