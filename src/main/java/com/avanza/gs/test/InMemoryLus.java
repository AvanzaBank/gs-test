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

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;

import com.gigaspaces.grid.lookup.ServiceGridRegistrar;
import com.gigaspaces.internal.lookup.RegistrarFactory;
import com.gigaspaces.start.SystemInfo;
import com.sun.jini.reggie.GigaRegistrar;

import net.jini.core.discovery.LookupLocator;

/**
 * This starts a LUS in the JVM. It will fail if a LUS is already started.
 * <p>
 * This LUS is configured for unicast only and the locator can be retrieved with {@link #getLocator()}.
 *
 * @see <a href="https://docs.gigaspaces.com/latest/admin/the-lookup-service.html">GigaSpaces documentation</a>
 */
final class InMemoryLus implements AutoCloseable {

	private final ServiceGridRegistrar lus;

	public InMemoryLus() {
		if (GigaRegistrar.isActive()) {
			throw new IllegalStateException("LUS is already active, cannot initialize another LUS in the same JVM");
		}
		setLusSystemProperties();
		lus = startLus();
		// Register locator in GigaSpaces singleton to override value set during GigaSpaces initialization
		SystemInfo.singleton().lookup().setLocators(getLocatorAsString());
	}

	private static ServiceGridRegistrar startLus() {
		try {
			return (ServiceGridRegistrar) RegistrarFactory.createRegistrar();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void setLusSystemProperties() {
		// Disable multicast
		System.setProperty("com.gs.multicast.enabled", "false");
	}

	public LookupLocator getLocator() {
		try {
			return lus.getLocator();
		} catch (NoSuchObjectException e) {
			throw new RuntimeException(e);
		}
	}

	public String getLocatorAsString() {
		LookupLocator locator = getLocator();
		return locator.getHost() + ":" + locator.getPort();
	}

	@Override
	public void close() throws RemoteException {
		lus.destroy();
	}
}
