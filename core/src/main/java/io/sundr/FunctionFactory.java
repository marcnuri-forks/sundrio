/*
 * Copyright 2016 The original authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.sundr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class FunctionFactory<X,Y,K> implements Function<X,Y> {

    private final Map<K,Y> cache;
    private final CacheFunction<K, Y> cacheFunction;
    private final CacheKeyFunction<X, Y, K> cacheKeyFunction;
    private final Function<X,Y> function;
    private final Function<X,Y> fallback;
    private final Function<X, Boolean> fallbackPredicate;
    private final int maximumRecursionLevel;
    private final int maximumNestingDepth;


    private final Stack<X> ownStack;
    private static final Stack globalStack = new Stack();

    public FunctionFactory(Map<K, Y> cache, Function<X, Y> function, Function<X, Y> fallback, Function<X, Boolean> fallbackPredicate, int maximumRecursionLevel, int maximumNestingDepth, Stack<X> ownStack) {
        this(cache,
            (item, value) -> (K)item,
            function, fallback, fallbackPredicate, maximumRecursionLevel, maximumNestingDepth, ownStack
        );
    }

    public FunctionFactory(Map<K, Y> cache,
        CacheKeyFunction<X, Y, K> cacheKeyFunction, Function<X, Y> function,
        Function<X, Y> fallback, Function<X, Boolean> fallbackPredicate, int maximumRecursionLevel,
        int maximumNestingDepth, Stack<X> ownStack) {
        this(cache,
            putIfCacheAndKeyNotNull(),
            cacheKeyFunction,
            function, fallback, fallbackPredicate, maximumRecursionLevel, maximumNestingDepth,
            ownStack
        );
    }

    public FunctionFactory(Map<K, Y> cache, CacheFunction<K, Y> cacheFunction,
        CacheKeyFunction<X, Y, K> cacheKeyFunction, Function<X, Y> function,
        Function<X, Y> fallback, Function<X, Boolean> fallbackPredicate, int maximumRecursionLevel,
        int maximumNestingDepth, Stack<X> ownStack) {
        this.cache = cache;
        this.cacheFunction = cacheFunction;
        this.cacheKeyFunction = cacheKeyFunction;
        this.function = function;
        this.fallback = fallback;
        this.fallbackPredicate = fallbackPredicate;
        this.maximumRecursionLevel = maximumRecursionLevel;
        this.maximumNestingDepth = maximumNestingDepth;
        this.ownStack = ownStack;
    }

    public Y apply(X item) {
        Y result;
        synchronized (function) {
            ownStack.push(item);
            globalStack.push(item);
            try {
                result = cache != null ? cache.get(item) : null;
                if (result == null) {
                    int recursionLevel = ownStack != null ? Collections.frequency(ownStack, item) : 0;
                    int nestingDepth = globalStack.size();
                    boolean recursionLevelExceeded = recursionLevel > maximumRecursionLevel && maximumRecursionLevel > 0;
                    boolean nestringDeptExceeded =nestingDepth > maximumNestingDepth && maximumNestingDepth > 0;
                    boolean predicateMatched = fallbackPredicate != null && fallbackPredicate.apply(item);
                    if ((recursionLevelExceeded || nestringDeptExceeded || predicateMatched) && fallback != null) {
                        result = fallback.apply(item);
                    }  else {
                        result = function.apply(item);
                        cacheFunction.cache(cache, cacheKeyFunction.getKey(item, result), result);
                    }
                }
            } finally {
                ownStack.pop();
                globalStack.pop();
            }
            return result;
        }
    }

    @FunctionalInterface
    public interface CacheKeyFunction<X, Y, K> {
        K getKey(X item, Y result);
    }

    private static <K, V> CacheFunction<K, V> putIfCacheAndKeyNotNull() {
        return (cacheInstance, key, value) -> Optional.ofNullable(cacheInstance)
            .map(c -> key).ifPresent(k -> cacheInstance.put(key, value));
    }

    @FunctionalInterface
    public interface CacheFunction<K, V> {
        void cache(Map<K, V> cache, K key, V value);
    }

    public static <X, Y> FunctionFactory<X, Y, X> cache(Function<X, Y> function) {
        return new FunctionFactory<X, Y, X>(new HashMap<X, Y>(), function, null, null, 0, 0, new Stack<X>());
    }

    public static <X, Y, K> FunctionFactory<X, Y, K> cache(Function<X, Y> function,
        CacheKeyFunction<X, Y, K> cacheKeyFunction) {
        return new FunctionFactory<>(new HashMap<>(), cacheKeyFunction, function,
            null, null, 0, 0, new Stack<X>());
    }

    public static <X, Y, K> FunctionFactory<X, Y, K> cache(Function<X, Y> function,
        CacheFunction<K, Y> cacheFunction, CacheKeyFunction<X, Y, K> cacheKeyFunction) {
        return new FunctionFactory<>(new HashMap<>(), cacheFunction, cacheKeyFunction, function,
            null, null, 0, 0, new Stack<X>());
    }

    public static <X, Y> FunctionFactory<X, Y, X> wrap(Function<X, Y> function) {
        return new FunctionFactory<X, Y, X>(null, function, null, null, 0, 0, new Stack<X>());
    }

    public FunctionFactory<X, Y, K> withFallback(Function<X,Y> fallback) {
        return new FunctionFactory<X, Y, K>(cache, function, fallback, fallbackPredicate, maximumRecursionLevel, maximumNestingDepth, ownStack);
    }

    public FunctionFactory<X, Y, K> withMaximumRecursionLevel(int maximumRecursionLevel) {
        return new FunctionFactory<X, Y, K>(cache, function, fallback, fallbackPredicate, maximumRecursionLevel, maximumNestingDepth, ownStack);
    }

    public FunctionFactory<X, Y, K> withMaximumNestingDepth(int maximumNestingDepth) {
        return new FunctionFactory<X, Y, K>(cache, function, fallback, fallbackPredicate, maximumRecursionLevel, maximumNestingDepth, ownStack);
    }

    public FunctionFactory<X, Y, K> withFallbackPredicate(Function<X,Boolean> fallbackPredicate) {
        return new FunctionFactory<X, Y, K>(cache, function, fallback, fallbackPredicate, maximumRecursionLevel, maximumNestingDepth, ownStack);
    }
}
