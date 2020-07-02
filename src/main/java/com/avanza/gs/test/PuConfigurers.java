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

public class PuConfigurers {

	static {
		// This needs to be executed before anything about GS is initialized,
		// such as the static block in JVMGlobalLus
		setSystemProperties();
	}

	private static void setSystemProperties() {
		if (System.getProperty("java.rmi.server.hostname") == null) {
			// Overrides values resolved by GigaSpaces
			// {@link NIOInfoHelper#getLocalHostAddress} and
			// {@link NIOInfoHelper#getLocalHostName} via
			// {@link BootUtil#getHost}
			System.setProperty("java.rmi.server.hostname", "localhost");
		}
	}

	public static PartitionedPuConfigurer partitionedPu(String puXmlPath) {
		return new PartitionedPuConfigurer(puXmlPath);
	}

	public static MirrorPuConfigurer mirrorPu(String puXmlPath) {
		return new MirrorPuConfigurer(puXmlPath);
	}

}
