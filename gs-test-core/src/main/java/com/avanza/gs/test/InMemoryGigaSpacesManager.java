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

import static com.gigaspaces.start.SystemInfo.LOOKUP_LOCATORS_SYS_PROP;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gigaspaces.CommonSystemProperties;

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
	private final Path gsHome;

	public InMemoryGigaSpacesManager() {
		this.gsHome = setupGsHome();
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

	/**
	 * Sets up temporary GS_HOME in order to avoid files being created in project directory during tests.
	 * If GS_HOME property is already set prior to this initialization, nothing will be done and cleanup will be left to
	 * the external source.
	 */
	private Path setupGsHome() {
		if (System.getProperty(CommonSystemProperties.GS_HOME) != null) {
			return null;
		}
		try {
			Path tmpPath = Files.createTempDirectory(getClass().getSimpleName());
			System.setProperty(CommonSystemProperties.GS_HOME, tmpPath.toString());
			return tmpPath;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static void setGigaSpacesManagerProperties(Path zooKeeperConfig) {
		// Set manager to be available with ZooKeeper on localhost
		System.setProperty("com.gs.manager.servers", "localhost");
		System.setProperty("com.gs.zookeeper.config-file", zooKeeperConfig.toString());

		// Disable reporting to HSQLDB
		System.setProperty("com.gs.hsqldb.all-metrics-recording.enabled", "false");
		System.setProperty("com.gs.ops-ui.enabled", "false");

		String lookupLocators = System.getProperty(LOOKUP_LOCATORS_SYS_PROP);
		if (lookupLocators != null) {
			// this property being set prior to the test causes constructor in
			// com.gigaspaces.start.SystemInfo.XapLookup to throw an IllegalStateException
			LOG.info("System property \"{}={}\" was set, this causes issues during test initialization combined with "
							+ "GigaSpaces Manager properties. Clearing ...",
					LOOKUP_LOCATORS_SYS_PROP, lookupLocators);
			System.clearProperty(LOOKUP_LOCATORS_SYS_PROP);
		}
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
		// Delete temporary GS_HOME
		if (gsHome != null) {
			try (Stream<Path> s = Files.walk(gsHome)) {
				s.sorted(Comparator.reverseOrder())
						.forEach(p -> {
							try {
								Files.delete(p);
							} catch (IOException e) {
								throw new UncheckedIOException("Failed to delete " + p, e);
							}
						});
			} catch (Exception e) {
				LOG.warn("Failed deleting GS_HOME at {}", gsHome, e);
			}
		}
	}
}
