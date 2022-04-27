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

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

/**
 * This interface makes sure that {@link #before} and {@link #after} are called
 * at most once, regardless of whether the implementing class is used in tests
 * as a {@code static} instance (which would normally use the callbacks from
 * {@link BeforeAllCallback}), or as a non-{@code static} instance (which would
 * normally use {@link BeforeEachCallback}).
 */
interface ResourceExtension extends BeforeAllCallback, BeforeEachCallback {

	void before() throws Exception;

	void after() throws Exception;

	@Override
	default void beforeAll(ExtensionContext context) throws Exception {
		ensureInitialized(context);
	}

	@Override
	default void beforeEach(ExtensionContext context) throws Exception {
		if (context.getParent().map(this::getStore).map(store -> store.get(this)).isEmpty()) {
			ensureInitialized(context);
		}
	}

	private Store getStore(ExtensionContext context) {
		return context.getStore(Namespace.create(this, context.getRequiredTestClass()));
	}

	private void ensureInitialized(ExtensionContext context) throws Exception {
		Store store = getStore(context);
		if (store.get(this) == null) {
			before();
			store.put(this, (Store.CloseableResource) this::after);
		}
	}

}
