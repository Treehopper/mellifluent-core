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

import java.lang.annotation.Annotation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;

public interface IGenerateSelfOverrideMethodBuilder {

  default CtMethod<Object> build() {
    CtMethod<Object> selfMethod = getTypeFactory().createMethod();
    selfMethod.setSimpleName("self");
    selfMethod.addModifier(ModifierKind.PROTECTED);
    CtTypeReference<Annotation> overrideReference =
        getTypeFactory().createCtTypeReference(Override.class);
    selfMethod.addAnnotation(getTypeFactory().createAnnotation(overrideReference));
    CtBlock<Object> block = getTypeFactory().createBlock();
    CtReturn<Object> returnStatement = getTypeFactory().createReturn();
    CtLocalVariableReference<Object> thisExpression =
        getTypeFactory().createLocalVariableReference();
    thisExpression.setSimpleName("this");
    CtVariableRead<Object> thisRead = getTypeFactory().createVariableRead();
    thisRead.setVariable(thisExpression);
    returnStatement.setReturnedExpression(thisRead);
    block.addStatement(returnStatement);
    selfMethod.setBody(block);
    selfMethod.setType(getBuilderReference());
    return selfMethod;
  }

  CtTypeReference<Object> getBuilderReference();

  Factory getTypeFactory();
}
