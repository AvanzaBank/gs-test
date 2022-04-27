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

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is intended to simulate an in-memory version of GigaSpaces Manager, and starts the following components:
 * <ul>
 *     <li>an in-memory LUS</li>
 *     <li>an in-memory ZooKeeper instance</li>
 * </ul>
 * Usage of ZooKeeper can be disabled by a system property.
 * <p>
 * Other components that are part of the GigaSpaces Manager, such as GSM and Rest API, are not started.
 *
 * @see <a href="https://docs.gigaspaces.com/latest/admin/xap-manager.html">GigaSpaces Manager documentation</a>
 */
final class InMemoryGigaSpacesManager implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(InMemoryGigaSpacesManager.class);
	private static final String DISABLE_ZOOKEEPER_PROPERTY = "com.avanza.gs.test.zookeeper.disable";

	private final InMemoryZooKeeper zooKeeper;
	private final InMemoryLus lus;

	public InMemoryGigaSpacesManager() {
		if (shouldStartZooKeeper()) {
			this.zooKeeper = new InMemoryZooKeeper();
			setGigaSpacesManagerProperties(zooKeeper.getZooKeeperConfig());
		} else {
			this.zooKeeper = null;
		}
		this.lus = new InMemoryLus();
	}

	public InMemoryLus getLus() {
		return lus;
	}

	private static boolean shouldStartZooKeeper() {
		return !Boolean.getBoolean(DISABLE_ZOOKEEPER_PROPERTY);
	}

	private static void setGigaSpacesManagerProperties(Path zooKeeperConfig) {
		// Set manager to be available with ZooKeeper on localhost
		System.setProperty("com.gs.manager.servers", "localhost");
		System.setProperty("com.gs.zookeeper.config-file", zooKeeperConfig.toString());

		// Disable reporting to HSQLDB
		System.setProperty("com.gs.hsqldb.all-metrics-recording.enabled", "false");
		System.setProperty("com.gs.ops-ui.enabled", "false");
	}

	@Override
	public void close() {
		try {
			lus.close();
		} catch (Exception e) {
			LOG.warn("Error while closing LUS", e);
		}
		if (zooKeeper != null) {
			try {
				zooKeeper.close();
			} catch (Exception e) {
				LOG.warn("Error while closing ZooKeeper server", e);
			}
		}
	}
}
