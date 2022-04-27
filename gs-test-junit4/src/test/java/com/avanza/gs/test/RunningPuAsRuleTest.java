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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openspaces.core.GigaSpace;

import com.avanza.gs.test.helpers.FruitPojo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunningPuAsRuleTest {

	@Rule
	public final RunningPu runningPu = PuConfigurers.partitionedPu("/fruit-pu.xml")
			.numberOfPrimaries(1)
			.numberOfBackups(0)
			.configure();

	@Test
	public void test_1_saveAndRead() {
		GigaSpace gigaSpace = runningPu.getClusteredGigaSpace();
		gigaSpace.write(new FruitPojo("apple"));

		FruitPojo fruit = gigaSpace.readById(FruitPojo.class, "apple");
		assertNotNull(fruit);
	}

	@Test
	public void test_2_shouldBeResetBetweenTests() {
		GigaSpace gigaSpace = runningPu.getClusteredGigaSpace();
		FruitPojo fruit = gigaSpace.readById(FruitPojo.class, "apple");
		assertNull(fruit);
	}
}
