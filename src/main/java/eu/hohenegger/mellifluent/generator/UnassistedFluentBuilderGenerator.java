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
package eu.hohenegger.mellifluent.generator;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Named;

import eu.hohenegger.mellifluent.generator.api.FIBuilderBuilder;
import eu.hohenegger.mellifluent.generator.api.FIFieldBuilder;
import eu.hohenegger.mellifluent.generator.api.FIFieldWriteBuilder;
import eu.hohenegger.mellifluent.generator.api.FIGenerateSelfOverrideMethodBuilder;
import eu.hohenegger.mellifluent.generator.api.FIInstantiationBuilder;
import eu.hohenegger.mellifluent.generator.api.FIWithPropertyMethodBuilder;
import eu.hohenegger.mellifluent.generator.model.Util;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.declaration.CtMethodImpl;

@Named("UnassistedFluentBuilderGenerator")
public class UnassistedFluentBuilderGenerator<T extends Class> extends AbstractFluentGenerator<T> {

    @Override
    protected void postRewrite() {
        model.getRootPackage().addPackage(builderPackage);
    }

    @Override
    protected CtClass<?> rewriteClass(CtType<T> buildable, boolean includeInheritedMethods) {
        String builderName = buildable.getSimpleName() + "Builder";

        CtClass<?> builderClass = typeFactory.createClass(builderName);
        builderClass.setSuperclass(abstractBuilder.getReference());
        List<CtTypeParameter> buildableClassTypeParameters = buildable.getFormalCtTypeParameters();
        builderClass.setFormalCtTypeParameters(buildableClassTypeParameters);
        Set<String> buildableClassTypeParameterNames = buildableClassTypeParameters.stream()
                .map(CtNamedElement::getSimpleName).collect(toSet());

        CtMethod<?> selfOverrideMethod = new FIGenerateSelfOverrideMethodBuilder()
                .withTypeFactory(typeFactory)
                .withBuilderReference(builderClass.getReference())
                .build();
        builderClass.addMethod(selfOverrideMethod);
        
        FIBuilderBuilder builderBuilder = new FIBuilderBuilder().withBuilderClass(builderClass);
        List<CtMethod<?>> constructors = buildable.getMethodsByName(buildable.getSimpleName());
        for (CtMethod<?> constructor : constructors) {
            List<CtParameter<?>> parameters = constructor.getParameters();
            for (CtParameter<?> parameter : parameters) {
                // TODO: create withMethod for each constructor parameter
            }
        }

        List<CtInvocation<Object>> setterCalls = new ArrayList<>();

        Set<CtMethod<?>> methods = includeInheritedMethods ? buildable.getAllMethods() : buildable.getMethods();
        for (CtMethod<?> method : methods) {
            if (!method.getSimpleName().startsWith("set")) {
                continue;
            }
            List<CtParameter<?>> parameters = method.getParameters();
            if (parameters.size() != 1) {
                continue;
            }

            CtParameter<?> parameter = parameters.get(0);
            CtTypeReference<?> parameterType = parameter.getType();
            if (parameterType.isGenerics()) {
                Optional<String> methodLevelTypeParameter = findLeaveGenerics(parameterType)
                        .stream()
                        .map(CtTypeReference::getSimpleName)
                        .filter(not(buildableClassTypeParameterNames::contains))
                        .findFirst();
                
                if (methodLevelTypeParameter.isPresent()) {
                    continue;
                }
            }
            CtField<Object> propertyField = new FIFieldBuilder()
                    .withTypeFactory(typeFactory)
                    .withSimpleName(Util.extractPropertyName(method))
                    .withGetterDeclaringType(parameterType)
                    .build();
            builderClass.addField(propertyField);

            setterCalls.add(createSetterCall(method, propertyField));

            CtVariableAccess<Object> fieldWrite = new FIFieldWriteBuilder()
                    .withTypeFactory(typeFactory)
                    .withFieldName(propertyField.getSimpleName())
                    .build();

            CtMethod<Object> withPropertyMethod = new FIWithPropertyMethodBuilder()
                    .withTypeFactory(typeFactory)
                    .withFieldWrite(fieldWrite)
                    .withBuilder(builderClass)
                    .withPropertyName(Util.extractPropertyName(method))
                    .withPropertyField(propertyField)
                    .withAbstractBuilder(abstractBuilder)
                    .build();

            builderClass.addMethod(withPropertyMethod);

        }

        CtMethod<?> buildMethod = createBuildMethod(buildable.getReference(),
                createBuildMethodBody(buildable.getReference(), setterCalls));
        builderBuilder
            .withBuildMethod(buildMethod)
            .withGeneratedByMetaData(getClass().getCanonicalName());

        abstractBuilder.putMetadata(GENERATED_BY, getClass().getCanonicalName());
        CtType<Object> build = builderBuilder.build();
        build.setParent(builderPackage);
        builderPackage.addType(build);

        builderPackage.addType(abstractBuilder.clone());

        return builderClass;
    }

    private List<CtTypeReference<?>> findLeaveGenerics(CtTypeReference<?> parameterType) {
        List<CtTypeReference<?>> typeArguments = parameterType.getActualTypeArguments();
        if (typeArguments.isEmpty()) {
            return Collections.singletonList(parameterType);
        }

        return typeArguments
                .stream()
                .filter(CtTypeInformation::isGenerics)
                .map(ctTypeReference -> findLeaveGenerics(ctTypeReference))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private CtInvocation<Object> createSetterCall(CtMethod method, CtField<Object> propertyField) {
        CtInvocation<Object> setterInvocation = typeFactory.createInvocation();
        setterInvocation.setExecutable(method.getReference());

        CtVariableRead<Object> read = typeFactory.createVariableRead();
        read.setVariable((CtVariableReference<Object>) propertyField.getReference());

        setterInvocation.setArguments(Collections.singletonList(read));
        return setterInvocation;
    }

    private CtStatement createBuildMethodBody(CtTypeReference builderType, List<CtInvocation<Object>> setterCalls) {
        CtBlock<?> block = typeFactory.createBlock();

        CtReturn<Object> methodReturn = typeFactory.createReturn();

        CtLocalVariable localVariableImpl = new CtLocalVariableImpl<>();
        localVariableImpl.setType(builderType);
        localVariableImpl.setSimpleName("result");
        block.addStatement(localVariableImpl);
        
        CtAssignment<Object, Object> assignment = new FIInstantiationBuilder()
            .withTypeFactory(typeFactory)
            .withName("result")
            .withTypeReference(builderType)
            .build();
        
        block.addStatement(assignment);

        for (CtInvocation<Object> setterCall : setterCalls) {
            CtLocalVariableReference resultExpression = typeFactory.createLocalVariableReference();
            resultExpression.setSimpleName("result");
            CtVariableRead<Object> parameterRead = typeFactory.createVariableRead();
            parameterRead.setVariable(resultExpression);

            setterCall.setTarget(parameterRead);
            block.addStatement(setterCall);
        }

        CtVariableRead<Object> read = typeFactory.createVariableRead();
        read.setVariable((CtVariableReference<Object>) localVariableImpl.getReference());
        methodReturn.setReturnedExpression(read);
        block.addStatement(methodReturn);

        return block;
    }

    private CtMethod<?> createBuildMethod(CtTypeReference type, CtStatement body) {
        CtMethodImpl<?> buildMethod = new CtMethodImpl<>()
            .setDefaultMethod(false)
            .addModifier(ModifierKind.PUBLIC);
        buildMethod.setBody(body);
        buildMethod.setSimpleName("build");
        buildMethod.setType(type);
        return buildMethod;
    }

    @Override
    protected Filter<CtType<?>> createFilter(String packageName) {
        return new FilterForRegularClasses<T>(packageName);
    }

    @Override
    CtPackage getGeneratedPackage() {
        return model.getRootPackage().clone();
    }
}
