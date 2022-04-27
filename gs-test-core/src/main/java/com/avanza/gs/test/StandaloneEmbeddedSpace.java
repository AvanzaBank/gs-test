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

import java.util.UUID;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

public class StandaloneEmbeddedSpace {

	private final UrlSpaceConfigurer urlSpaceConfigurer;
	private final GigaSpace gigaSpace;

	public StandaloneEmbeddedSpace() {
		this("space-" + UUID.randomUUID());
	}
	
	public StandaloneEmbeddedSpace(String spaceName) {
		this.urlSpaceConfigurer = new UrlSpaceConfigurer("/./" + spaceName)
				.lookupLocators(JVMGlobalGigaSpacesManager.getLookupLocator());
		this.gigaSpace = new GigaSpaceConfigurer(urlSpaceConfigurer.space()).gigaSpace();
	}
	
	public GigaSpace getGigaSpace() {
		return gigaSpace;
	}
	
	public void start() {		
	}
	
	public void destroy() {
		urlSpaceConfigurer.close();
	}

}
