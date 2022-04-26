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

import java.io.IOException;
import java.util.Properties;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.properties.BeanLevelProperties;
import org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainer;
import org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainerProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public class MirrorPu implements PuRunner {

	private IntegratedProcessingUnitContainer container;
	private final String gigaSpaceBeanName = "gigaSpace";
	private final String puXmlPath;
	private final Resource puConfigResource;
	private final Properties contextProperties;
	private final String lookupLocator;
	private final boolean autostart;
	private final ApplicationContext parentContext;
	
	public MirrorPu(AbstractMirrorPuConfigurer config) {
		this.puXmlPath = config.puXmlPath;
		this.puConfigResource = config.puConfigResource;
		this.contextProperties = config.properties;
		this.lookupLocator = config.lookupLocator != null ? config.lookupLocator : JVMGlobalGigaSpacesManager.getLookupLocator();
		this.autostart = true;
		this.parentContext = config.parentContext;
		this.contextProperties.put("gs.space.url.arg.timeout", "10");
		this.contextProperties.put("gs.space.url.arg.locators", lookupLocator);
	}

	@Override
	public void run() {
		try {
			startContainers();
		} catch (Exception e) {
			throw new RuntimeException("Failed to start containers for puXmlPath: " + puXmlPath, e);
		}
	}

	private void startContainers() throws IOException {
		IntegratedProcessingUnitContainerProvider provider = new IntegratedProcessingUnitContainerProvider();
		provider.setBeanLevelProperties(createBeanLevelProperties());
		if (puXmlPath == null && puConfigResource == null) {
			throw new IllegalArgumentException("Either puXmlPath or puConfigResource needs to be set");
		}
 		if (puXmlPath != null) {
			provider.addConfigLocation(puXmlPath);
		}
		if (puConfigResource != null) {
			provider.addConfigLocation(puConfigResource);
		}
		if (parentContext != null) {
			provider.setParentContext(parentContext);
		}
		container = (IntegratedProcessingUnitContainer) provider.createContainer();
	}

	private BeanLevelProperties createBeanLevelProperties() {
		BeanLevelProperties beanLevelProperties = new BeanLevelProperties();		
		beanLevelProperties.setContextProperties(contextProperties);
		return beanLevelProperties;
	}
	
	@Override
	public void shutdown() {
		container.close();
	}

	@Override
	public String getLookupLocator() {
		return this.lookupLocator;
	}
	
	@Override
	public boolean autostart() {
		return this.autostart;
	}
	
	@Override
	public GigaSpace getClusteredGigaSpace() {
		return container.getApplicationContext().getBean(this.gigaSpaceBeanName, GigaSpace.class).getClustered();
	}
	
	@Override
	public ApplicationContext getPrimaryInstanceApplicationContext(int partition) {
		return container.getApplicationContext();
	}

	@Override
	public ApplicationContext getBackupInstanceApplicationContext(int partition, int backup) {
		throw new UnsupportedOperationException("Mirror PU does not contain any backup");
	}

	@Override
	public int getNumInstances() {
		return 1;
	}
}
