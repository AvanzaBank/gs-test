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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import com.avanza.gs.test.helpers.FruitPojo;
import com.avanza.gs.test.helpers.TestSpaceSynchronizationEndpoint;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.DataSyncOperationType;

public class PuWithMirrorTest {

	@Rule
	public final RunningPu fruitPu = PuConfigurers.partitionedPu("/fruit-pu.xml")
			.numberOfPrimaries(1)
			.numberOfBackups(1)
			.configure();

	@Rule
	public final RunningPu mirrorPu = PuConfigurers.mirrorPu("/fruit-mirror-pu.xml")
			.configure();

	@Test
	public void testDataIsPersistedToMirror() {
		GigaSpace gigaSpace = fruitPu.getClusteredGigaSpace();
		gigaSpace.write(new FruitPojo("apple"));

		FruitPojo fruit = gigaSpace.readById(FruitPojo.class, "apple");
		assertNotNull(fruit);

		TestSpaceSynchronizationEndpoint spaceSynchronizationEndpoint = mirrorPu.getPrimaryInstanceApplicationContext(0)
				.getBean(TestSpaceSynchronizationEndpoint.class);

		await().until(spaceSynchronizationEndpoint::getDataSyncOperations, not(empty()));

		DataSyncOperation dataSyncOperation = spaceSynchronizationEndpoint.getDataSyncOperations().get(0);
		assertThat(dataSyncOperation.getDataSyncOperationType(), is(DataSyncOperationType.WRITE));
		assertThat((FruitPojo) dataSyncOperation.getDataAsObject(), is(new FruitPojo("apple")));
	}
}
