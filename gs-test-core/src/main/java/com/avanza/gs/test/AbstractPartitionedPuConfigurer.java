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

public final class AbstractPartitionedPuConfigurer {
	
	String puXmlPath;
	Resource puConfigResource;
	int numberOfPrimaries = 1;
	int numberOfBackups = 0;
	boolean startAsync = false;
	Properties contextProperties = new Properties();
	Map<String, Properties> beanProperies = new HashMap<>();
	String lookupLocator;
	String spaceName = "test-space";
	public boolean autostart = true;
	ApplicationContext parentContext;
	boolean useAuthentication;

	public AbstractPartitionedPuConfigurer(String puXmlPath) {
		this.puXmlPath = puXmlPath;
	}

	public AbstractPartitionedPuConfigurer(Resource puConfigResource) {
		this.puConfigResource = puConfigResource;
	}

	public AbstractPartitionedPuConfigurer parentContext(ApplicationContext parentContext) {
		this.parentContext = parentContext;
		return this;
	}

	public AbstractPartitionedPuConfigurer numberOfPrimaries(int numberOfPrimaries) {
		this.numberOfPrimaries = numberOfPrimaries;
		return this;
	}

	public AbstractPartitionedPuConfigurer numberOfBackups(int numberOfBackups) {
		this.numberOfBackups = numberOfBackups;
		return this;
	}

	public AbstractPartitionedPuConfigurer beanProperties(String beanName, Properties beanProperties) {
		this.beanProperies.put(beanName, beanProperties);
		return this;
	}

	public AbstractPartitionedPuConfigurer startAsync(boolean startAsync) {
		this.startAsync  = startAsync;
		return this;
	}

	/**
	 * @deprecated lookupGroup for multicast is no longer in use and have been replaced by
	 *             {@link #lookupLocator(String)} for unicast. Calling this method will have no effect.
	 */
	@Deprecated
	public AbstractPartitionedPuConfigurer lookupGroup(String group) {
		return this;
	}

	public AbstractPartitionedPuConfigurer lookupLocator(String lookupLocator) {
		this.lookupLocator = lookupLocator;
		return this;
	}

	public GenericRunningPu configure() {
		if (startAsync) {
			return new StandaloneRunningPu(new AsyncPuRunner(new PartitionedPu(this)));
		}
		return new StandaloneRunningPu(new PartitionedPu(this));
	}

	public AbstractPartitionedPuConfigurer contextProperties(Properties properties) {
		this.contextProperties = properties;
		return this;
	}
	
	public AbstractPartitionedPuConfigurer contextProperties(ContextProperties properties) {
		this.contextProperties = properties.getProperties();
		return this;
	}

	public AbstractPartitionedPuConfigurer contextProperty(String name, String value) {
		this.contextProperties.setProperty(name, value);
		return this;
	}

	public AbstractPartitionedPuConfigurer autostart(boolean autostart) {
		this.autostart = autostart;
		return this;
	}

	public AbstractPartitionedPuConfigurer spaceName(String spaceName) {
		this.spaceName = spaceName;
		return this;
	}

	public AbstractPartitionedPuConfigurer withAuthentication() {
		this.useAuthentication = true;
		return this;
	}
}
