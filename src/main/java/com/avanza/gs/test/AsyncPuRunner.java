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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class AsyncPuRunner implements PuRunner {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final PuRunner puRunner;
	private volatile ExecutorService worker;
	
	public AsyncPuRunner(PuRunner puRunner) {
		this.puRunner = puRunner;
	}

	@Override
	public void run() throws Exception {
		worker = Executors.newSingleThreadExecutor();
		worker.execute(() -> {
			try {
				puRunner.run();
			} catch (Exception e) {
				log.debug("Error running PU", e);
			}
		});
	}

	@Override
	public void shutdown() throws Exception {
		Future<Void> shutdownComplete = worker.submit(() -> {
			puRunner.shutdown();
			return null;
		});
		worker.shutdown();
		shutdownComplete.get();
	}

	@Override
	public String getLookupGroupName() {
		return puRunner.getLookupGroupName();
	}
	
	@Override
	public boolean autostart() {
		return puRunner.autostart();
	}

	@Override
	public GigaSpace getClusteredGigaSpace() {
		return get(() -> puRunner.getClusteredGigaSpace());
	}
	
	private <T> T get(Callable<T> callable) {
		try {
			return worker.submit(callable).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ApplicationContext getPrimaryInstanceApplicationContext(int partition) {
		return get(() -> puRunner.getPrimaryInstanceApplicationContext(partition));
	}

	@Override
	public ApplicationContext getBackupInstanceApplicationContext(int partition, int backup) {
		return get(() -> puRunner.getBackupInstanceApplicationContext(partition, backup));
	}

	@Override
	public int getNumInstances() {
		return puRunner.getNumInstances();
	}
}
