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

import java.util.Set;

import javax.inject.Named;

import eu.hohenegger.mellifluent.generator.api.FIBuildMethodBuilder;
import eu.hohenegger.mellifluent.generator.api.FIBuilderBuilder;
import eu.hohenegger.mellifluent.generator.api.FIBuilderClassBuilder;
import eu.hohenegger.mellifluent.generator.api.FIFieldBuilder;
import eu.hohenegger.mellifluent.generator.api.FIFieldWriteBuilder;
import eu.hohenegger.mellifluent.generator.api.FIGenerateSelfOverrideMethodBuilder;
import eu.hohenegger.mellifluent.generator.api.FIGetterBuilder;
import eu.hohenegger.mellifluent.generator.api.FIWithPropertyMethodBuilder;
import eu.hohenegger.mellifluent.generator.model.Util;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.Filter;

@Named("FluentBuilderGenerator")
public class FluentBuilderGenerator<T extends Class> extends AbstractFluentGenerator<T> {

    @Override
    protected void postRewrite() {
        model.getRootPackage().addPackage(builderPackage);
    }

    @Override
    protected CtClass<?> rewriteClass(CtType<T> buildable, boolean includeInheritedMethods) {
        String builderName = "F" + buildable.getSimpleName();

        CtClass<?> builderClass = new FIBuilderClassBuilder()
                .withTypeFactory(typeFactory)
                .withBuilderName(builderName)
                .withAbstractBuilderReference(abstractBuilder.getReference())
                .withBuildableReference(buildable.getReference())
                .build();
        builderClass.setVisibility(ModifierKind.PUBLIC);

        CtMethod<?> selfOverrideMethod = new FIGenerateSelfOverrideMethodBuilder()
                .withTypeFactory(typeFactory)
                .withBuilderReference(builderClass.getReference())
                .build();
        builderClass.addMethod(selfOverrideMethod);

        FIBuilderBuilder builderBuilder = new FIBuilderBuilder()
                .withBuilderClass(builderClass)
                .withSelfOverrideMethod(selfOverrideMethod);

        Set<CtMethod<?>> methods = buildable.getMethods();
        for (CtMethod<?> method : methods) {
            if (!method.getSimpleName().startsWith("get")) {
                continue;
            }
            CtField<Object> propertyField = new FIFieldBuilder()
                    .withTypeFactory(typeFactory)
                    .withSimpleName(Util.extractPropertyName(method))
                    .withGetterDeclaringType(method.getType())
                    .build();
            builderClass.addField(propertyField);

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

            // override getter from interface ====================
            CtMethod<?> overriddenGetter = new FIGetterBuilder()
                    .withTypeFactory(typeFactory)
                    .withInterfaceMethod(method)
                    .withField(propertyField)
                    .build();
            builderClass.addMethod(overriddenGetter);
        }

        CtMethod<?> buildMethod = new FIBuildMethodBuilder().withBuildable(buildable).build();
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

    @Override
    protected Filter<CtType<?>> createFilter(String packageName) {
        return new FilterForInterfacesWithTwoADefaultMethod<T>(packageName);
    }

    @Override
    CtPackage getGeneratedPackage() {
        return model.getRootPackage().clone();
    }
}
