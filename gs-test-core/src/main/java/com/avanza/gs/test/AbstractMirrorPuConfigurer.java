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

import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public abstract class AbstractMirrorPuConfigurer<T extends AbstractMirrorPuConfigurer<T, S>, S extends GenericRunningPu> implements MirrorConfig {

	private final String puXmlPath;
	private final Resource puConfigResource;
	private Properties properties = new Properties();
	private ApplicationContext parentContext;
	private String lookupLocator;

	public AbstractMirrorPuConfigurer(String puXmlPath) {
		this.puXmlPath = puXmlPath;
		this.puConfigResource = null;
	}

	public AbstractMirrorPuConfigurer(Resource puConfigResource) {
		this.puXmlPath = null;
		this.puConfigResource = puConfigResource;
	}

	/**
	 * @deprecated Use {@link #contextProperty(String, String)} instead.
	 * E.g. {@code .contextProperty("configSourceId", serviceRegistry.getConfigSourceId())) }
	 */
	@Deprecated
	public T contextProperties(Properties properties) {
		this.properties = properties;
		return me();
	}

	/**
	 * @deprecated Use {@link #contextProperty(String, String)} instead.
	 * E.g. {@code .contextProperty("configSourceId", serviceRegistry.getConfigSourceId())) }
	 */
	@Deprecated
	public T contextProperties(ContextProperties properties) {
		this.properties = properties.getProperties();
		return me();
	}

	public T contextProperty(String propertyName, String propertyValue) {
		this.properties.setProperty(propertyName, propertyValue);
		return me();
	}

	public T parentContext(ApplicationContext parentContext) {
		this.parentContext = parentContext;
		return me();
	}

	/**
	 * @deprecated lookupGroup for multicast is no longer in use and have been replaced by
	 *             {@link #lookupLocator(String)} for unicast. Calling this method will have no effect.
	 */
	@Deprecated
	public T lookupGroup(String group) {
		return me();
	}
		
	public T lookupLocator(String lookupLocator) {
		this.lookupLocator = lookupLocator;
		return me();
	}
	
	public abstract S configure();

	protected PuRunner createPuRunner() {
		return new MirrorPu(this);
	}

	@Override
	public String getPuXmlPath() {
		return puXmlPath;
	}

	@Override
	public Resource getPuConfigResource() {
		return puConfigResource;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public ApplicationContext getParentContext() {
		return parentContext;
	}

	@Override
	public String getLookupLocator() {
		return lookupLocator;
	}

	@SuppressWarnings("unchecked")
	private T me() {
		return (T) this;
	}

}
