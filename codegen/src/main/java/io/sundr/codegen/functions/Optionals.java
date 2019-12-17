/*
 *      Copyright 2016 The original authors.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package io.sundr.codegen.functions;

import io.sundr.Function;
import io.sundr.FunctionFactory;
import io.sundr.FunctionFactory.CacheKeyFunction;
import io.sundr.codegen.model.ClassRef;
import io.sundr.codegen.model.TypeDef;
import io.sundr.codegen.model.TypeRef;
import io.sundr.codegen.utils.TypeUtils;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static io.sundr.codegen.functions.ClassTo.TYPEDEF;

public class Optionals {

    public static final TypeDef OPTIONAL = TYPEDEF.apply(Optional.class);
    public static final TypeDef OPTIONAL_INT = TYPEDEF.apply(OptionalInt.class);
    public static final TypeDef OPTIONAL_DOUBLE = TYPEDEF.apply(OptionalDouble.class);
    public static final TypeDef OPTIONAL_LONG = TYPEDEF.apply(OptionalLong.class);

    private static <V> CacheKeyFunction<TypeRef, V, String> cacheKeyForTypeRef() {
        return (typeRef, value) -> Optional.of(typeRef)
            .filter(ClassRef.class::isInstance)
            .map(ClassRef.class::cast).map(ClassRef::getFullyQualifiedName).orElse(null);
    }

    public static final Function<TypeRef, Boolean> IS_OPTIONAL = FunctionFactory.cache(
        type -> TypeUtils.isInstanceOf(type, OPTIONAL, Optionals.IS_OPTIONAL),
        cacheKeyForTypeRef()
    );

    public static final Function<TypeRef, Boolean> IS_OPTIONAL_INT = FunctionFactory.cache(
        type -> TypeUtils.isInstanceOf(type, OPTIONAL_INT, Optionals.IS_OPTIONAL_INT),
        cacheKeyForTypeRef()
    );

    public static final Function<TypeRef, Boolean> IS_OPTIONAL_DOUBLE = FunctionFactory.cache(
        type -> TypeUtils.isInstanceOf(type, OPTIONAL_DOUBLE, Optionals.IS_OPTIONAL_DOUBLE),
        cacheKeyForTypeRef()
    );

    public static final Function<TypeRef, Boolean> IS_OPTIONAL_LONG = FunctionFactory.cache(
        type -> TypeUtils.isInstanceOf(type, OPTIONAL_LONG, Optionals.IS_OPTIONAL_LONG),
        cacheKeyForTypeRef()
    );

}
