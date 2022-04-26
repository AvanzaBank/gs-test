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

public class AbstractMirrorPuConfigurer {

	String puXmlPath;
	Resource puConfigResource;
	Properties properties = new Properties();
	ApplicationContext parentContext;
	String lookupLocator;

	public AbstractMirrorPuConfigurer(String puXmlPath) {
		this.puXmlPath = puXmlPath;
	}

	public AbstractMirrorPuConfigurer(Resource puConfigResource) {
		this.puConfigResource = puConfigResource;
	}

	public AbstractMirrorPuConfigurer contextProperty(String propertyName, String propertyValue) {
		this.properties.setProperty(propertyName, propertyValue);
		return this;
	}

	public AbstractMirrorPuConfigurer parentContext(ApplicationContext parentContext) {
		this.parentContext = parentContext;
		return this;
	}

	/**
	 * @deprecated lookupGroup for multicast is no longer in use and have been replaced by
	 *             {@link #lookupLocator(String)} for unicast. Calling this method will have no effect.
	 */
	@Deprecated
	public AbstractMirrorPuConfigurer lookupGroup(String group) {
		return this;
	}
		
	public AbstractMirrorPuConfigurer lookupLocator(String lookupLocator) {
		this.lookupLocator = lookupLocator;
		return this;
	}
	
	public GenericRunningPu configure() {
		return new StandaloneRunningPu(new MirrorPu(this));
	}

}
