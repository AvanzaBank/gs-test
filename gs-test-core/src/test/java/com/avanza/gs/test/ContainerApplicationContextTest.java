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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.openspaces.core.GigaSpace;
import org.springframework.context.ApplicationContext;

public class ContainerApplicationContextTest {

	private static final String SPACE_NAME = "fruit-space";

	@Test
	public void testFetchingApplicationContextWithBackup() throws Exception {
		try (RunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
				.numberOfPrimaries(2)
				.numberOfBackups(1)
				.configure()) {
			fruitPu.start();

			ApplicationContext primaryContext1 = fruitPu.getPrimaryInstanceApplicationContext(0);
			ApplicationContext primaryContext2 = fruitPu.getPrimaryInstanceApplicationContext(1);
			ApplicationContext backupContext1 = fruitPu.getBackupInstanceApplicationContext(0, 1);
			ApplicationContext backupContext2 = fruitPu.getBackupInstanceApplicationContext(1, 1);

			// Verify that the context for the correct container is fetched
			assertThat(containerName(primaryContext1), is(expectedContainerName(1)));
			assertThat(containerName(primaryContext2), is(expectedContainerName(2)));
			assertThat(containerName(backupContext1), is(expectedContainerName(1, 1)));
			assertThat(containerName(backupContext2), is(expectedContainerName(2, 1)));

			// Verify fetching invalid contexts should fail validation
			assertThrows(IllegalArgumentException.class, () -> fruitPu.getPrimaryInstanceApplicationContext(-1));
			assertThrows(IllegalArgumentException.class, () -> fruitPu.getPrimaryInstanceApplicationContext(2));
			assertThrows(IllegalArgumentException.class, () -> fruitPu.getBackupInstanceApplicationContext(0, 0));
			assertThrows(IllegalArgumentException.class, () -> fruitPu.getBackupInstanceApplicationContext(0, 2));
		}
	}

	@Test
	public void testFetchingApplicationContextWithoutBackup() throws Exception {
		try (RunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
				.numberOfPrimaries(2)
				.numberOfBackups(0)
				.configure()) {
			fruitPu.start();

			ApplicationContext primaryContext1 = fruitPu.getPrimaryInstanceApplicationContext(0);
			ApplicationContext primaryContext2 = fruitPu.getPrimaryInstanceApplicationContext(1);

			// Verify that the context for the correct container is fetched
			assertThat(containerName(primaryContext1), is(expectedContainerName(1)));
			assertThat(containerName(primaryContext2), is(expectedContainerName(2)));

			// Verify fetching backup context should fail validation when configured without backup
			assertThrows(IllegalArgumentException.class, () -> fruitPu.getBackupInstanceApplicationContext(0, 1));
			assertThrows(IllegalArgumentException.class, () -> fruitPu.getBackupInstanceApplicationContext(1, 1));
		}
	}

	private static String containerName(ApplicationContext context) {
		return context.getBean(GigaSpace.class).getSpace().getContainerName();
	}

	private static String expectedContainerName(int partition) {
		return SPACE_NAME + "_container" + partition;
	}

	// GigaSpaces format for container name contains partition and backup number like space_container1_1
	private static String expectedContainerName(int partition, int backup) {
		return expectedContainerName(partition) + "_" + backup;
	}

}
