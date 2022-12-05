package com.avanza.gs.test.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avanza.gs.test.StandaloneEmbeddedSpace;

public class GigaspaceExtension extends StandaloneEmbeddedSpace implements ResourceExtension, ParameterResolver
{
	private static final Logger LOG = LoggerFactory.getLogger(GigaspaceExtension.class);
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.getParameter().getType().equals(GigaSpace.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return getGigaSpace();
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
}
