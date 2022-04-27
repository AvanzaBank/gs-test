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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openspaces.core.GigaSpace;
import org.openspaces.zookeeper.leader_selector.ZooKeeperBasedLeaderSelectorHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.avanza.gs.test.helpers.FruitPojo;
import com.gigaspaces.internal.server.space.SpaceImpl;

public class ZooKeeperLeaderSelectorTest {

	@Test
	public void testZooKeeperFailover() throws Exception {
		try (RunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
				.numberOfPrimaries(1)
				.numberOfBackups(1)
				.configure()) {
			fruitPu.start();

			ApplicationContext primaryContext = fruitPu.getPrimaryInstanceApplicationContext(0);
			ApplicationContext backupContext = fruitPu.getBackupInstanceApplicationContext(0, 1);

			getClusteredGigaSpace(primaryContext).write(new FruitPojo("apple"));

			// Verify that primary & backup are configured as expected first
			assertThat(getSpaceImpl(primaryContext).isPrimary(), is(true));
			assertThat(getSpaceImpl(backupContext).isBackup(), is(true));

			// Verify that ZooKeeper is used for selecting leader
			assertThat(getSpaceImpl(primaryContext).getLeaderSelector(), instanceOf(ZooKeeperBasedLeaderSelectorHandler.class));

			// Shut down primary instance, ZooKeeper should handle failover to the backup instance

			// This causes the following log to appear later on in the test, which can be ignored:
			//
			// [Curator-LeaderSelector-1] ERROR org.apache.curator.framework.recipes.leader.LeaderSelector - The leader threw an exception
			// java.lang.IllegalStateException: Expected state [STARTED] was [STOPPED]
			//     at ...
			((AbstractApplicationContext) primaryContext).close();

			// Wait for backup to be configured as primary
			await().until(() -> getSpaceImpl(backupContext).isPrimary());

			// Should be able to read object written to primary earlier
			FruitPojo fruit = getClusteredGigaSpace(backupContext).readById(FruitPojo.class, "apple");
			assertThat(fruit, notNullValue());

			// Start the original primary instance again, it should now become backup
			((AbstractApplicationContext) primaryContext).refresh();
			await().until(() -> getSpaceImpl(primaryContext).isBackup());
		}
	}

	private static SpaceImpl getSpaceImpl(ApplicationContext context) {
		return context.getBean(GigaSpace.class).getSpace().getDirectProxy().getSpaceImplIfEmbedded();
	}

	private static GigaSpace getClusteredGigaSpace(ApplicationContext context) {
		return context.getBean(GigaSpace.class).getClustered();
	}

}
