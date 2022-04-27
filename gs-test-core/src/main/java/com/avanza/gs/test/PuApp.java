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

import static com.avanza.gs.test.PuXmlEmulation.createPuXmlResource;

import java.io.IOException;

import com.avanza.gs.test.StandalonePuConfigurers.StandalonePartitionedPuConfigurer;

public class PuApp implements AutoCloseable {

	private final PartitionedPu partitionedPu;

	private PuApp(String puXmlPath) {
		partitionedPu = new PartitionedPu(new StandalonePartitionedPuConfigurer(puXmlPath)
				.numberOfPrimaries(1)
				.numberOfBackups(0));
	}

	private PuApp(Class<?> puConfig) {
		partitionedPu = new PartitionedPu(new StandalonePartitionedPuConfigurer(createPuXmlResource(puConfig))
				.numberOfPrimaries(1)
				.numberOfBackups(0));
	}

	public static PuApp run(String puXmlPath) {
		try {
			PuApp puApp = new PuApp(puXmlPath);
			puApp.start();
			return puApp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static PuApp run(Class<?> puConfig) {
		try {
			PuApp puApp = new PuApp(puConfig);
			puApp.start();
			return puApp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void start() throws IOException {
		this.partitionedPu.run();
	}
	
	public void stop() {
		this.partitionedPu.shutdown();
	}

	@Override
	public void close() {
		stop();
	}

}
