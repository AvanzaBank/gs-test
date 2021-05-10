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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

interface ResourceExtension extends ChainableBeforeCallbacks {

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
			// cannot use computeIfAbsent because before declares checked exception
			before();
			store.put(this, (Store.CloseableResource) this::after);
		}
	}

}
