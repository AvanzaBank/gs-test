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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openspaces.core.GigaSpace;

import com.avanza.gs.test.junit5.helpers.FruitPojo;

public class EmbeddedSpaceAsRuleTest {

	@RegisterExtension
	public final EmbeddedSpace embeddedSpace = new EmbeddedSpace("space-name");

	@Test
	public void testPersistingAndReading() {
		GigaSpace gigaSpace = embeddedSpace.getGigaSpace();
		gigaSpace.write(new FruitPojo("orange"));

		FruitPojo fruit = gigaSpace.readById(FruitPojo.class, "orange");
		assertNotNull(fruit);
	}
}
