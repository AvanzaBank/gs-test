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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.properties.BeanLevelProperties;
import org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainer;
import org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainerProvider;
import org.openspaces.pu.container.support.CompoundProcessingUnitContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.gigaspaces.security.directory.DefaultCredentialsProvider;

/**
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public final class PartitionedPu implements PuRunner {

	private CompoundProcessingUnitContainer container;
	private final String gigaSpaceBeanName = "gigaSpace";
	private final String puXmlPath;
	private final Resource puConfigResource;
	private final Integer numberOfPrimaries;
	private final Integer numberOfBackups;
	private final Properties contextProperties = new Properties();
	private final Map<String, Properties> beanProperties = new HashMap<>();
	private final String lookupLocator;
	private final boolean autostart;
	private final ApplicationContext parentContext;
	private final boolean useAuthentication;

	public PartitionedPu(PartitionedPuConfigurer configurer) {
		this.puXmlPath = configurer.puXmlPath;
		this.puConfigResource = configurer.puConfigResource;
		this.numberOfBackups = configurer.numberOfBackups;
		this.numberOfPrimaries = configurer.numberOfPrimaries;
		this.contextProperties.putAll(configurer.contextProperties);
		this.beanProperties.putAll(configurer.beanProperies);
		this.lookupLocator = configurer.lookupLocator != null ? configurer.lookupLocator : JVMGlobalGigaSpacesManager.getLookupLocator();
		this.autostart = configurer.autostart;
		this.parentContext = configurer.parentContext;
		this.useAuthentication = configurer.useAuthentication;
		this.contextProperties.put("spaceName", UniqueSpaceNameLookup.getSpaceNameWithSequence(configurer.spaceName));
		this.contextProperties.put("gs.space.url.arg.locators", lookupLocator);
		this.contextProperties.put("gs.space.url.arg.timeout", "10");

		validateConfiguration();
	}

	private void validateConfiguration() {
		if (puXmlPath == null && puConfigResource == null) {
			throw new IllegalArgumentException("Either puXmlPath or puConfigResource needs to be set");
		}
		if (numberOfPrimaries <= 0) {
			throw new IllegalArgumentException("Cannot be configured to have 0 or less primary instances");
		}
		if (numberOfBackups < 0) {
			throw new IllegalArgumentException("A negative amount of backup instances is not valid");
		}
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
		provider.setClusterInfo(createClusterInfo());
		if (puXmlPath != null) {
			provider.addConfigLocation(puXmlPath);
		}
		if (puConfigResource != null) {
			provider.addConfigLocation(puConfigResource);
		}
		if (parentContext != null) {
			provider.setParentContext(parentContext);
		}
		if (useAuthentication) {
			enableAuthentication(provider);
		}
		container = (CompoundProcessingUnitContainer) provider.createContainer();
	}

	private void enableAuthentication(IntegratedProcessingUnitContainerProvider provider) {
		final Properties contextProperties = provider.getBeanLevelProperties().getContextProperties();
		contextProperties.put("com.gs.security.security-manager.class", new SecurityManagerForTests());
		// SecurityManagerForTests will accept all credentials, so these can be anything
		final DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider(SecurityManagerForTests.TEST_USER, SecurityManagerForTests.TEST_PASS);
		provider.setCredentialsProvider(credentialsProvider);
		// Despite its name, this property is read by com.avanza.astrix.gs.ClusteredProxyCacheImpl
		System.getProperties().put("com.gs.security.credentials-provider.class", credentialsProvider);

		// Override default timeouts that are being used when security is enabled
		// during PU startup.
		// SpaceRemoteOperationsExecutorsClusterConfig defaults to 20s
		contextProperties.setProperty("space-config.proxy.router.active-server-lookup-timeout", "300");
		// ClusterXML::createActiveElectConfig used by ActiveElectionManager::sleepYieldTime defaults to 1s
		contextProperties.setProperty("cluster-config.groups.group.fail-over-policy.active-election.yield-time", "100");
	}

	private ClusterInfo createClusterInfo() {
		ClusterInfo clusterInfo = new ClusterInfo();
		clusterInfo.setSchema("partitioned-sync2backup");
		clusterInfo.setNumberOfInstances(numberOfPrimaries);
		clusterInfo.setNumberOfBackups(numberOfBackups);
		clusterInfo.setInstanceId(null);
		return clusterInfo;
	}

	private BeanLevelProperties createBeanLevelProperties() {
		BeanLevelProperties beanLevelProperties = new BeanLevelProperties();
		beanLevelProperties.setContextProperties(contextProperties);
		beanProperties.forEach(beanLevelProperties::setBeanProperties);
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
		return this.autostart ;
	}
	
	@Override
	public GigaSpace getClusteredGigaSpace() {
		return getPrimaryInstanceApplicationContext(0).getBean(this.gigaSpaceBeanName, GigaSpace.class).getClustered();
	}

	@Override
	public ApplicationContext getPrimaryInstanceApplicationContext(int partition) {
		if (partition >= numberOfPrimaries || partition < 0) {
			throw new IllegalArgumentException("Invalid primary partition argument: " + partition + ", valid numbers are 0 to " + (numberOfPrimaries - 1));
		}
		int containerNo = getContainerNoForPrimaryInstance(partition);
		IntegratedProcessingUnitContainer container = (IntegratedProcessingUnitContainer) this.container.getProcessingUnitContainers()[containerNo];
		return container.getApplicationContext();
	}

	@Override
	public ApplicationContext getBackupInstanceApplicationContext(int partition, int backup) {
		if (numberOfBackups == 0) {
			throw new IllegalArgumentException("No backup instances have been configured, cannot fetch backup instance context");
		}
		if (backup > numberOfBackups || backup <= 0) {
			throw new IllegalArgumentException("Invalid backup instance argument: " + backup + ", valid numbers are 1 to " + numberOfBackups);
		}
		int containerNo = getContainerNoForPrimaryInstance(partition) + backup;
		IntegratedProcessingUnitContainer container = (IntegratedProcessingUnitContainer) this.container.getProcessingUnitContainers()[containerNo];
		return container.getApplicationContext();
	}

	private int getContainerNoForPrimaryInstance(int partition) {
		return partition * (numberOfBackups + 1);
	}

	@Override
	public int getNumInstances() {
		return numberOfPrimaries;
	}
}
