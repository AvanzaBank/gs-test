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

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Keeps a singleton instance of the GigaSpaces Manager used for tests.
 * <p>
 * The same instance will be used for all tests, and closed with the JVM in a shutdown hook.
 */
public final class JVMGlobalGigaSpacesManager {

	private static volatile InMemoryGigaSpacesManager instance;

	private static InMemoryGigaSpacesManager getInstance() {
		if (instance == null) {
			synchronized (JVMGlobalGigaSpacesManager.class) {
				if (instance == null) {
					instance = new InMemoryGigaSpacesManager();
					Runtime.getRuntime().addShutdownHook(new Thread(() -> instance.close()));
				}
			}
		}
		return instance;
	}

	private static Optional<String> getConfiguredGsManagerLookupLocators() {
		if (System.getProperty("com.gs.manager.servers") != null) {
			int port = Integer.parseInt(System.getProperty("com.sun.jini.reggie.initialUnicastDiscoveryPort", "4174"));
			return Optional.of(Stream.of(System.getProperty("com.gs.manager.servers").split(","))
					.map(server -> server.replace("localhost", "127.0.0.1") + ":" + port)
					.distinct()
					.sorted()
					.collect(Collectors.joining(",")));
		}
		return Optional.empty();
	}

	public static String getLookupLocator() {
		return getConfiguredGsManagerLookupLocators()
				.orElseGet(() -> getInstance().getLus().getLocatorAsString());
	}
}
