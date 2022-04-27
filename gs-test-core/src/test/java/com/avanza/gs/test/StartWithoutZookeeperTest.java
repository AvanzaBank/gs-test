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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.openspaces.core.GigaSpace;
import org.springframework.context.ApplicationContext;

import com.avanza.gs.test.helpers.FruitPojo;
import com.gigaspaces.cluster.activeelection.LusBasedSelectorHandler;
import com.gigaspaces.internal.server.space.SpaceImpl;

public class StartWithoutZookeeperTest {

	@Test
	@Ignore("This test is disabled as it can't run in the same JVM as the Zookeeper-enabled tests. "
			+ "GigaSpaces only supports a single setup in a single runtime. "
			+ "Therefore, this test can only be run manually as a separate test")
	public void testStartingWithLusBasedSelectorHandler() throws Exception {
		// Setting this system property will start without Zookeeper, causing LUS to be used as a selector handler
		System.setProperty("com.avanza.gs.test.zookeeper.disable", "true");

		try (GenericRunningPu fruitPu = StandalonePuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
				.numberOfPrimaries(1)
				.numberOfBackups(1)
				.configure()) {
			fruitPu.start();

			ApplicationContext primaryContext = fruitPu.getPrimaryInstanceApplicationContext(0);
			ApplicationContext backupContext = fruitPu.getBackupInstanceApplicationContext(0, 1);

			GigaSpace gigaSpace = fruitPu.getClusteredGigaSpace();

			gigaSpace.write(new FruitPojo("apple"));

			// Verify that primary & backup are configured as expected first
			assertThat(getSpaceImpl(primaryContext).isPrimary(), is(true));
			assertThat(getSpaceImpl(backupContext).isBackup(), is(true));

			// Verify that LUS is used for selecting leader
			assertThat(getSpaceImpl(primaryContext).getLeaderSelector(), instanceOf(LusBasedSelectorHandler.class));

			// Should be able to read object written to primary
			FruitPojo fruit = gigaSpace.readById(FruitPojo.class, "apple");
			assertThat(fruit, notNullValue());
		}
	}

	private static SpaceImpl getSpaceImpl(ApplicationContext context) {
		return context.getBean(GigaSpace.class).getSpace().getDirectProxy().getSpaceImplIfEmbedded();
	}

}
