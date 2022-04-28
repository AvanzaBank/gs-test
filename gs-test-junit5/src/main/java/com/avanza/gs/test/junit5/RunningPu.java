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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avanza.gs.test.PuRunner;
import com.avanza.gs.test.StandaloneRunningPu;

/**
 * JUnit5 {@code @Extension} implementation of {@code StandaloneRunningPu}.
 * <p>
 * Example usage in a JUnit 5 test class:
 * <pre>
 * &#64;RegisterExtension
 * RunningPu runningPu = PuConfigurers.partitionedPu("pu.xml")
 *                                    .configure();
 * </pre>
 */
public final class RunningPu extends StandaloneRunningPu implements ResourceExtension {

	private static final Logger LOG = LoggerFactory.getLogger(RunningPu.class);

	public RunningPu(PuRunner runner) {
		super(runner);
	}

	@Override
	public void before() throws Exception {
		if (isAutostart()) {
			start();
		}
	}

	@Override
	public void after() {
		try {
			stop();
		} catch (Exception e) {
			LOG.warn("Error while shutting down PU", e);
		}
	}
}
