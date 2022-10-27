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

public final class PartitionedPuConfigurer {
	
	final String puXmlPath;
	final Resource puConfigResource;
	int numberOfPrimaries = 1;
	int numberOfBackups = 0;
	boolean startAsync = false;
	Properties contextProperties = new Properties();
	Map<String, Properties> beanProperties = new HashMap<>();
	String lookupGroupName = JVMGlobalLus.getLookupGroupName();
	String spaceName = "test-space";
	public boolean autostart = true;
	ApplicationContext parentContext;
	boolean useAuthentication;

	public PartitionedPuConfigurer(String puXmlPath) {
		this.puXmlPath = puXmlPath;
		this.puConfigResource = null;
	}

	public PartitionedPuConfigurer(Resource puConfigResource) {
		this.puXmlPath = null;
		this.puConfigResource = puConfigResource;
	}
	
	public PartitionedPuConfigurer parentContext(ApplicationContext parentContext) {
		this.parentContext = parentContext;
		return this;
	}

	public PartitionedPuConfigurer numberOfPrimaries(int numberOfPrimaries) {
		this.numberOfPrimaries = numberOfPrimaries;
		return this;
	}

	public PartitionedPuConfigurer numberOfBackups(int numberOfBackups) {
		this.numberOfBackups = numberOfBackups;
		return this;
	}

	public PartitionedPuConfigurer beanProperties(String beanName, Properties beanProperties) {
		this.beanProperties.put(beanName, beanProperties);
		return this;
	}

	public PartitionedPuConfigurer startAsync(boolean startAsync) {
		this.startAsync  = startAsync;
		return this;
	}

	public PartitionedPuConfigurer lookupGroup(String group) {
		this.lookupGroupName = group;
		return this;
	}

	/**
	 * @deprecated For backwards compatibility only. Use {@link #lookupGroup(String)} instead
	 */
	@Deprecated
	public PartitionedPuConfigurer groupName(String group) {
		this.lookupGroupName = group;
		return this;
	}

	public RunningPu configure() {
		if (startAsync) {
			return new RunningPuImpl(new AsyncPuRunner(new PartitionedPu(this)));
		}
		return new RunningPuImpl(new PartitionedPu(this));
	}

	/**
	 * @deprecated Use {@link #contextProperty(String, String)} instead.
	 * E.g. {@code .contextProperty("configSourceId", serviceRegistry.getConfigSourceId())) }
	 */
	@Deprecated
	public PartitionedPuConfigurer contextProperties(Properties properties) {
		this.contextProperties = properties;
		return this;
	}

	/**
	 * @deprecated Use {@link #contextProperty(String, String)} instead.
	 * E.g. {@code .contextProperty("configSourceId", serviceRegistry.getConfigSourceId())) }
	 */
	@Deprecated
	public PartitionedPuConfigurer contextProperties(ContextProperties properties) {
		this.contextProperties = properties.getProperties();
		return this;
	}

	public PartitionedPuConfigurer contextProperty(String name, String value) {
		this.contextProperties.setProperty(name, value);
		return this;
	}

	public PartitionedPuConfigurer autostart(boolean autostart) {
		this.autostart = autostart;
		return this;
	}

	public PartitionedPuConfigurer spaceName(String spaceName) {
		this.spaceName = spaceName;
		return this;
	}

	public PartitionedPuConfigurer withAuthentication() {
		this.useAuthentication = true;
		return this;
	}
}
