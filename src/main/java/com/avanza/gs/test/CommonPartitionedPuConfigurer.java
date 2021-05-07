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

import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class CommonPartitionedPuConfigurer<T extends CommonPartitionedPuConfigurer<T>> {
	
	protected final String puXmlPath;
	protected int numberOfPrimaries = 1;
	protected int numberOfBackups = 0;
	protected boolean startAsync = false;
	protected Properties contextProperties = new Properties();
	protected Map<String, Properties> beanProperties = new HashMap<>();
	protected String lookupGroupName = JVMGlobalLus.getLookupGroupName();
	protected String spaceName = "test-space";
	public boolean autostart = true;
	protected ApplicationContext parentContext;
	protected boolean useAuthentication;

	protected CommonPartitionedPuConfigurer(String puXmlPath) {
		this.puXmlPath = puXmlPath;
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

	public T lookupGroup(String group) {
		this.lookupGroupName = group;
		return me();
	}

	/**
	 * @deprecated For backwards compatibility only. Use {@link #lookupGroup(String)} instead
	 */
	@Deprecated
	public T groupName(String group) {
		this.lookupGroupName = group;
		return me();
	}

	public T contextProperties(Properties properties) {
		this.contextProperties = properties;
		return me();
	}
	
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
	
	@SuppressWarnings("unchecked")
	private T me() {
		return (T) this;
	}
}
