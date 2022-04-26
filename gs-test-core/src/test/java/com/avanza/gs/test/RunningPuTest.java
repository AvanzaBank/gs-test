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

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspaces.core.GigaSpace;

import com.avanza.gs.test.helpers.FruitPojo;
import com.avanza.gs.test.helpers.GigaSpacesTestUtil;
import com.avanza.gs.test.helpers.TestSpaceSynchronizationEndpoint;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.DataSyncOperationType;

public class RunningPuTest {

	@Test
	public void testStartingPuWithPrimaryAndBackup() throws Exception {
		try (GenericRunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
				.numberOfPrimaries(1)
				.numberOfBackups(1)
				.configure()) {
			fruitPu.start();

			GigaSpace gigaSpace = fruitPu.getClusteredGigaSpace();
			gigaSpace.write(new FruitPojo("banana"));

			assertNotNull(gigaSpace.readById(FruitPojo.class, "banana"));

			// Verify SpaceURL properties
			assertThat(GigaSpacesTestUtil.getSpaceUrlProperties(gigaSpace), allOf(
					hasEntry("schema", "default"),
					hasEntry("container", "fruit-space_container1"),
					hasEntry("mirror", "true"),
					hasEntry("locators", JVMGlobalGigaSpacesManager.getLookupLocator()),
					hasEntry("total_members", "1,1"),
					hasEntry("space", "fruit-space"),
					hasEntry("cluster_schema", "partitioned-sync2backup"),
					hasEntry("id", "1"),
					hasEntry("state", "started"),
					hasEntry("membername", "fruit-space_container1:fruit-space")
			));
		}
	}

	@Test
	public void testStartingPuWithPrimaryAndMirror() throws Exception {
		try (GenericRunningPu mirrorPu = PuConfigurers.mirrorPu("classpath:/fruit-mirror-pu.xml").configure()) {
			try (GenericRunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
					.numberOfPrimaries(1)
					.numberOfBackups(1)
					.configure()) {

				mirrorPu.start();
				fruitPu.start();

				GigaSpace gigaSpace = fruitPu.getClusteredGigaSpace();
				gigaSpace.write(new FruitPojo("banana"));

				TestSpaceSynchronizationEndpoint spaceSynchronizationEndpoint = mirrorPu.getPrimaryInstanceApplicationContext(0)
								.getBean(TestSpaceSynchronizationEndpoint.class);

				await().until(spaceSynchronizationEndpoint::getDataSyncOperations, not(empty()));

				DataSyncOperation dataSyncOperation = spaceSynchronizationEndpoint.getDataSyncOperations().get(0);
				assertThat(dataSyncOperation.getDataSyncOperationType(), is(DataSyncOperationType.WRITE));
				assertThat((FruitPojo) dataSyncOperation.getDataAsObject(), is(new FruitPojo("banana")));

				// Verify MirrorPU SpaceURL properties
				assertThat(GigaSpacesTestUtil.getSpaceUrlProperties(mirrorPu.getClusteredGigaSpace()), allOf(
						hasEntry("schema", "mirror"),
						hasEntry("container", "fruit-space-mirror_container"),
						hasEntry("locators", JVMGlobalGigaSpacesManager.getLookupLocator()),
						hasEntry("space", "fruit-space-mirror"),
						hasEntry("state", "started"),
						hasEntry("membername", "fruit-space-mirror_container:fruit-space-mirror")
				));
			}
		}
	}

}
