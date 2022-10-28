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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StreamUtils;

/**
 * Utility class to create a partitioned space without backups.
 * <p>
 * Individual primary instances can be retrieved by using the {@code partition(ID)} method. An url to the
 * cluster can be retrieved through the {@code getUrl()} method.
 *
 * @deprecated This class is provided for backwards compatibility.
 * For a simple test with no need for partitions, {@link EmbeddedSpace} can be used instead.
 * For more complex tests, use {@link PuConfigurers} to set up a {@code partitionedPu}.
 */
@Deprecated
public class PartitionedSpace implements TestRule {

	private final GigaSpace[] instances;
	private final RunningPu partitionedPu;
	private final Path puXml;
	private final String spaceName;
	private final int numberOfPartitions;

	private boolean started = false;

	/**
	 * Creates a partitioned space with a given number of partitions.
	 *
	 * @param numberOfPartitions (must be at least 2).
	 * @throws IllegalArgumentException if numberOfPartitions is less than 2.
	 */
	public PartitionedSpace(int numberOfPartitions) {
		this(numberOfPartitions, "testSpace");
	}

	/**
	 * Creates a partitioned space with a given number of partitions.
	 *
	 * @param numberOfPartitions (must be at least 2).
	 * @param spaceName name of the space to create.
	 *
	 * @throws IllegalArgumentException if numberOfPartitions is less than 2.
	 * @throws IllegalArgumentException if spaceName is empty string.
	 * @throws NullPointerException if spaceName is null.
	 */
	public PartitionedSpace(int numberOfPartitions, String spaceName) {
		this(numberOfPartitions, spaceName, null);
	}

	@Deprecated
	public PartitionedSpace(int numberOfPartitions, String spaceName, String lookupGroupName) {
		if (numberOfPartitions < 2) {
			throw new IllegalArgumentException("Number of partitions must be at least 2, was " + numberOfPartitions);
		}
		if (spaceName.isEmpty()) {
			throw new IllegalArgumentException("Space name must not be empty");
		}
		this.numberOfPartitions = numberOfPartitions;
		this.spaceName = spaceName;
		this.instances = new GigaSpace[numberOfPartitions];
		this.puXml = writePuXml(spaceName);
		this.partitionedPu = PuConfigurers.partitionedPu(new FileSystemResource(puXml.toFile()))
				.spaceName(spaceName)
				.numberOfPrimaries(numberOfPartitions)
				.numberOfBackups(0)
				.configure();

		start();
	}

	private static Path writePuXml(String spaceName) {
		try (InputStream in = PartitionedSpace.class.getResourceAsStream("/partitioned_space/simple-pu.xml.template")) {
			Path tempFile = Files.createTempFile("partitioned-space-", ".xml");
			String content = StreamUtils.copyToString(in, UTF_8)
					.replace("##SPACE_NAME##", spaceName);
			return Files.write(tempFile, content.getBytes(UTF_8));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void start() {
		if (started) {
			return;
		}
		try {
			partitionedPu.start();
		} catch (Exception e) {
			throw new RuntimeException("Could not start partitionedPu", e);
		}
		for (int instanceId = 1; instanceId <= numberOfPartitions; instanceId ++) {
			instances[instanceId - 1] = partitionedPu.getPrimaryInstanceApplicationContext(instanceId - 1)
					.getBean(GigaSpace.class);
		}
		started = true;
	}

	@Deprecated
	protected void configure(UrlSpaceConfigurer spaceConfigurer) {
		throw new UnsupportedOperationException("This method is not supported");
	}

	/**
	 * Returns the primary with the given instanceId.
	 *
	 * NOTE: Primaries are numbered from 1!
	 */
	public GigaSpace primary(int instanceId) {
		if (instanceId < 1) {
			throw new IllegalArgumentException("Primaries are numbered from 1! Requested instance was " + instanceId);
		}
		if (instanceId > instances.length) {
			throw new IllegalArgumentException("No such primary: " + instanceId + ". Space contains " + this.instances.length + " partitions.");
		}
		return instances[instanceId - 1];
	}

	private static final Object ALL_OBJECTS = null; /* Intentional NULL */

	/**
	 * Cleans all space's in this cluster.
	 */
	public void clean() {
		for (GigaSpace instance : instances) {
			instance.clear(ALL_OBJECTS);
		}
	}

	/**
	 * Returns an url to this space.
	 */
	public String getUrl() {
		return "jini://*/*/" + this.spaceName + "?locators=" + partitionedPu.getLookupLocator();
	}

	/**
	 * Creates a clustered proxy against this space.
	 */
	public GigaSpace createClusteredProxy() {
		return partitionedPu.getClusteredGigaSpace();
	}

	/**
	 * Destroys this space.
	 */
	public void destroy() {
		try {
			partitionedPu.stop();
			Files.deleteIfExists(puXml);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the name of this space.
	 */
	public String getName() {
		return this.spaceName;
	}

	/**
	 * Returns the lookup group name
	 */
	@Deprecated
	public String getLookupGroupName() {
		throw new UnsupportedOperationException("Lookup group is no longer used");
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					start();
					base.evaluate();
				} finally {
					destroy();
				}
			}
		};
	}

}
