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
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avanza.gs.test.StandaloneEmbeddedSpace;

/**
 * JUnit5 {@code Extension} implementation of {@code StandaloneEmbeddedSpace}.
 */
public final class EmbeddedSpace extends StandaloneEmbeddedSpace implements ResourceExtension, ParameterResolver {

	private static final Logger LOG = LoggerFactory.getLogger(EmbeddedSpace.class);

	public EmbeddedSpace() {
		super();
	}

	public EmbeddedSpace(String spaceName) {
		super(spaceName);
	}

	@Override
	public void before() {
		start();
	}

	@Override
	public void after() {
		try {
			destroy();
		} catch (Exception e) {
			LOG.warn("Error shutting down embedded space", e);
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.getParameter().getType().equals(GigaSpace.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return getGigaSpace();
	}
}
