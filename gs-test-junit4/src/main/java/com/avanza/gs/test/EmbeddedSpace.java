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
package com.avanza.gs.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * JUnit4 {@code TestRule} implementation of {@code StandaloneEmbeddedSpace}.
 */
public final class EmbeddedSpace extends StandaloneEmbeddedSpace implements TestRule {

	public EmbeddedSpace() {
		super();
	}

	public EmbeddedSpace(String spaceName) {
		super(spaceName);
	}

	@Override
	public EmbeddedSpace withLookupLocator(String lookupLocator) {
		super.withLookupLocator(lookupLocator);
		return this;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					start();
					base.evaluate();
				} finally {
					try {
						destroy();
					} catch (Exception e) {
						// Ignore
					}
				}
			}
		};
	}

}
