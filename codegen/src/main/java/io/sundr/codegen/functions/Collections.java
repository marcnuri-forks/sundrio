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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static io.sundr.codegen.functions.ClassTo.TYPEDEF;

public class Collections {

    private Collections() {
    }

    public static final TypeDef COLLECTION = TYPEDEF.apply(Collection.class);
    public static final TypeDef MAP = TYPEDEF.apply(Map.class);
    public static final TypeDef LINKED_HASH_MAP = TYPEDEF.apply(LinkedHashMap.class);
    public static final TypeDef LIST = TYPEDEF.apply(List.class);
    public static final TypeDef ARRAY_LIST = TYPEDEF.apply(ArrayList.class);
    public static final TypeDef SET = TYPEDEF.apply(Set.class);
    public static final TypeDef LINKED_HASH_SET = TYPEDEF.apply(LinkedHashSet.class);

    public static final Function<TypeRef, Boolean> IS_LIST = cache(
        type -> TypeUtils.isInstanceOf(type, LIST, Collections.IS_LIST));

    public static final Function<TypeRef, Boolean> IS_SET = cache(
        type -> TypeUtils.isInstanceOf(type, SET, Collections.IS_SET));

    public static final Function<TypeRef, Boolean> IS_MAP = cache(
        type -> TypeUtils.isInstanceOf(type, MAP, Collections.IS_MAP));

    public static final Function<TypeRef, Boolean> IS_COLLECTION = cache(
        type -> IS_LIST.apply(type) || IS_SET.apply(type));


    private static Function<TypeRef, Boolean> cache(Function<TypeRef, Boolean> function) {
        return FunctionFactory.cache(function, typeRefToKey());
    }

    private static CacheKeyFunction<TypeRef, Boolean, String> typeRefToKey() {
        return (item, value) -> Optional.ofNullable(item)
            .filter(ClassRef.class::isInstance)
            .map(ClassRef.class::cast).map(ClassRef::getFullyQualifiedName).orElse(null);
    }
}
