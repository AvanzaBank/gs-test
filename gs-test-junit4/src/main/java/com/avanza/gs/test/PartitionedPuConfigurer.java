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

import org.springframework.core.io.Resource;

public final class PartitionedPuConfigurer extends AbstractPartitionedPuConfigurer<PartitionedPuConfigurer, RunningPu> {
	public PartitionedPuConfigurer(String puXmlPath) {
		super(puXmlPath);
	}

	public PartitionedPuConfigurer(Resource puConfigResource) {
		super(puConfigResource);
	}

	@Override
	public RunningPu configure() {
		return new RunningPu(createPuRunner());
	}
}