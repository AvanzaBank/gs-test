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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Test;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.RemoteDataAccessException;

import com.avanza.gs.test.helpers.FruitPojo;
import com.avanza.gs.test.helpers.GigaSpacesTestUtil;

public class EmbeddedSpaceTest {

	private StandaloneEmbeddedSpace embeddedSpace;

	@After
	public void after() {
		if (embeddedSpace != null) {
			embeddedSpace.destroy();
		}
	}

	@Test
	public void createsASpace() {
		embeddedSpace = new StandaloneEmbeddedSpace();
		GigaSpace gigaSpace = embeddedSpace.getGigaSpace();

		FruitPojo pojo = new FruitPojo("banana");
		gigaSpace.write(pojo);
		FruitPojo result = gigaSpace.read(pojo);
		assertThat(result.getId(), is("banana"));
	}

	@Test
	public void destroysTheSpace() {
		embeddedSpace = new StandaloneEmbeddedSpace();
		GigaSpace gigaSpace = embeddedSpace.getGigaSpace();
		embeddedSpace.destroy();
		assertThrows(RemoteDataAccessException.class, () -> gigaSpace.write(new FruitPojo("apple")));
	}

	@Test
	public void createsASpaceWithGivenName() {
		embeddedSpace = new StandaloneEmbeddedSpace("custom-name");
		assertThat(embeddedSpace.getGigaSpace().getName(), is("custom-name"));
	}

	@Test
	public void isConfiguredWithCorrectSpaceUrl() {
		embeddedSpace = new StandaloneEmbeddedSpace("space");

		// Verify SpaceURL properties
		assertThat(GigaSpacesTestUtil.getSpaceUrlProperties(embeddedSpace.getGigaSpace()), allOf(
				hasEntry("schema", "default"),
				hasEntry("container", "space_container"),
				hasEntry("locators", JVMGlobalGigaSpacesManager.getLookupLocator()),
				hasEntry("space", "space"),
				hasEntry("state", "started"),
				hasEntry("membername", "space_container:space")
		));
	}

}
