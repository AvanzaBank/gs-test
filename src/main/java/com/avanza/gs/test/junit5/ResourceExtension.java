/*
 * Copyright 2017 Avanza Bank AB
 *
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
 */
package com.avanza.gs.test.junit5;

import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.util.ArrayList;
import java.util.List;

import static com.avanza.gs.test.junit5.ResourceExtension.Scope.ALL;
import static com.avanza.gs.test.junit5.ResourceExtension.Scope.EACH;

interface ResourceExtension extends BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    void before() throws Exception;

    void after() throws Exception;

    default ResourceExtension andThen(ResourceExtension other) {
        return new Order(this).andThen(other);
    }

    @Override
    default void beforeAll(ExtensionContext context) throws Exception {
        if (getOrSet(context, ALL) == ALL) {
            before();
        }
    }

    @Override
    default void beforeEach(ExtensionContext context) throws Exception {
        if (getOrSet(context, EACH) == EACH) {
            before();
        }
    }

    @Override
    default void afterEach(ExtensionContext context) throws Exception {
        if (getNullable(context) == EACH) {
            after();
        }
    }

    @Override
    default void afterAll(ExtensionContext context) throws Exception {
        if (getNullable(context) == ALL) {
            after();
        }
    }

    enum Scope {
        ALL, EACH
    }

    private Scope getOrSet(ExtensionContext context, Scope scope) {
        return getStore(context).getOrComputeIfAbsent(Scope.class, key -> scope, Scope.class);
    }

    private Scope getNullable(ExtensionContext context) {
        return getStore(context).get(Scope.class, Scope.class);
    }

    private Store getStore(ExtensionContext context) {
        return context.getRoot().getStore(Namespace.create(getClass(), context.getRequiredTestClass()));
    }

    final class Order implements ResourceExtension {

        private final List<ResourceExtension> order;

        public Order(List<ResourceExtension> order) {
            this.order = order;
        }

        Order(ResourceExtension first) {
            this(List.of(first));
        }

        @Override
        public void before() throws Exception {
            for (ResourceExtension resourceExtension : order) {
                resourceExtension.before();
            }
        }

        @Override
        public void after() throws Exception {
            for (ResourceExtension resourceExtension : order) {
                resourceExtension.after();
            }
        }

        @Override
        public ResourceExtension andThen(ResourceExtension other) {
            List<ResourceExtension> newOrder = new ArrayList<>(order);
            if (other instanceof Order) {
                newOrder.addAll(((Order) other).order);
            } else {
                newOrder.add(other);
            }
            return new Order(newOrder);
        }
    }
}