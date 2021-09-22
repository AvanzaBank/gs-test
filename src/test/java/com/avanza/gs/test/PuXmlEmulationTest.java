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

import static com.avanza.gs.test.PuXmlEmulation.createPuXmlConf;
import static java.nio.file.Files.readAllLines;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import org.springframework.context.annotation.Configuration;

public class PuXmlEmulationTest {
	@Test
	public void shouldProduceSpringXmlConfigThatReferencesSingleClass() throws Exception {
		// Act
		Path xmlFile = createPuXmlConf(SpringConfig.class);

		// Assert
		List<String> lines = readAllLines(xmlFile);
		assertThat(lines, hasItem("    <bean class=\"com.avanza.gs.test.PuXmlEmulationTest$SpringConfig\"/>"));
	}

	@Configuration
	public static class SpringConfig {
	}
}
