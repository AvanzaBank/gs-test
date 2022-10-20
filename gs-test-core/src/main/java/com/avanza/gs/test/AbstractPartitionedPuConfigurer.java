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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public abstract class AbstractPartitionedPuConfigurer<T extends AbstractPartitionedPuConfigurer<T, S>, S extends GenericRunningPu> implements PuConfig {

	private final String puXmlPath;
	private final Resource puConfigResource;
	private int numberOfPrimaries = 1;
	private int numberOfBackups = 0;
	private boolean startAsync = false;
	private Properties contextProperties = new Properties();
	private final Map<String, Properties> beanProperties = new HashMap<>();
	private String lookupLocator;
	private String spaceName = "test-space";
	private boolean autostart = true;
	private ApplicationContext parentContext;
	private boolean useAuthentication;

	public AbstractPartitionedPuConfigurer(String puXmlPath) {
		this.puXmlPath = puXmlPath;
		this.puConfigResource = null;
	}

	public AbstractPartitionedPuConfigurer(Resource puConfigResource) {
		this.puXmlPath = null;
		this.puConfigResource = puConfigResource;
	}

	public T parentContext(ApplicationContext parentContext) {
		this.parentContext = parentContext;
		return me();
	}

	public T numberOfPrimaries(int numberOfPrimaries) {
		this.numberOfPrimaries = numberOfPrimaries;
		return me();
	}

	public T numberOfBackups(int numberOfBackups) {
		this.numberOfBackups = numberOfBackups;
		return me();
	}

	public T beanProperties(String beanName, Properties beanProperties) {
		this.beanProperties.put(beanName, beanProperties);
		return me();
	}

	public T startAsync(boolean startAsync) {
		this.startAsync  = startAsync;
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
		if (startAsync) {
			return new AsyncPuRunner(new PartitionedPu(this));
		}
		return new PartitionedPu(this);
	}

	/**
	 * @deprecated Use {@link #contextProperty(String, String)} instead.
	 * E.g. {@code .contextProperty("configSourceId", serviceRegistry.getConfigSourceId())) }
	 */
	@Deprecated
	public T contextProperties(Properties properties) {
		this.contextProperties = properties;
		return me();
	}

	/**
	 * @deprecated Use {@link #contextProperty(String, String)} instead.
	 * E.g. {@code .contextProperty("configSourceId", serviceRegistry.getConfigSourceId())) }
	 */
	@Deprecated
	public T contextProperties(ContextProperties properties) {
		this.contextProperties = properties.getProperties();
		return me();
	}

	public T contextProperty(String name, String value) {
		this.contextProperties.setProperty(name, value);
		return me();
	}

	public T autostart(boolean autostart) {
		this.autostart = autostart;
		return me();
	}

	public T spaceName(String spaceName) {
		this.spaceName = spaceName;
		return me();
	}

	public T withAuthentication() {
		this.useAuthentication = true;
		return me();
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
	public int getNumberOfPrimaries() {
		return numberOfPrimaries;
	}

	@Override
	public int getNumberOfBackups() {
		return numberOfBackups;
	}

	@Override
	public Properties getContextProperties() {
		return contextProperties;
	}

	@Override
	public Map<String, Properties> getBeanProperties() {
		return beanProperties;
	}

	@Override
	public String getLookupLocator() {
		return lookupLocator;
	}

	@Override
	public String getSpaceName() {
		return spaceName;
	}

	@Override
	public boolean isAutostart() {
		return autostart;
	}

	@Override
	public ApplicationContext getParentContext() {
		return parentContext;
	}

	@Override
	public boolean isUseAuthentication() {
		return useAuthentication;
	}

	@SuppressWarnings("unchecked")
	private T me() {
		return (T) this;
	}
}
