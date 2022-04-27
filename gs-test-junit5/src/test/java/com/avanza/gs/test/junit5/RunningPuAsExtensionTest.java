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
package com.avanza.gs.test.junit5;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openspaces.core.GigaSpace;

import com.avanza.gs.test.junit5.helpers.FruitPojo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RunningPuAsExtensionTest {

	@RegisterExtension
	public final RunningPu runningPu = PuConfigurers.partitionedPu("/fruit-pu.xml")
			.numberOfPrimaries(1)
			.numberOfBackups(0)
			.configure();

	@Test
	@Order(1)
	public void testSaveAndRead() {
		GigaSpace gigaSpace = runningPu.getClusteredGigaSpace();
		gigaSpace.write(new FruitPojo("apple"));

		FruitPojo fruit = gigaSpace.readById(FruitPojo.class, "apple");
		assertNotNull(fruit);
	}

	@Test
	@Order(2)
	public void verifyPersistedDataIsResetBetweenTests() {
		GigaSpace gigaSpace = runningPu.getClusteredGigaSpace();
		FruitPojo fruit = gigaSpace.readById(FruitPojo.class, "apple");
		assertNull(fruit);
	}
}
