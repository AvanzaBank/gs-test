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

/**
 * Convenience class to use with PuConfigurers to avoid creating an anonymous subclass when setting context properties.
 *
 * @author Kristoffer Erlandsson (krierl)
 */
public class ContextProperties {

	private final Properties props = new Properties();

	public ContextProperties() {
	}

	/**
	 * Convenience constructor to add one property upon creation.
	 */
	public ContextProperties(String key, String value) {
		props.put(key, value);
	}

	public ContextProperties withProperty(String key, String value) {
		props.setProperty(key, value);
		return this;
	}

	public Properties getProperties() {
		return props;
	}
}
