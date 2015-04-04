/*
 * Copyright 2015 The original authors.
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

package io.sundr.builder.examples;

import io.sundr.builder.annotations.Buildable;

public class Circle extends Shape {

    private final Long radius;

    @Buildable
    public Circle(int x, int y, long radius) {
        super(x, y);
        this.radius = radius;
    }

    public Long getRadius() {
        return radius;
    }
}
