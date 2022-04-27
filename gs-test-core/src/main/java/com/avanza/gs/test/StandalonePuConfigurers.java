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

import org.springframework.core.io.Resource;

public class StandalonePuConfigurers {

	public static StandalonePartitionedPuConfigurer partitionedPu(String puXmlPath) {
		return new StandalonePartitionedPuConfigurer(puXmlPath);
	}

	public static StandalonePartitionedPuConfigurer partitionedPu(Resource puConfigResource) {
		return new StandalonePartitionedPuConfigurer(puConfigResource);
	}

	public static StandalonePartitionedPuConfigurer partitionedPu(Class<?> puConfig) {
		return partitionedPu(createPuXmlResource(puConfig));
	}

	public static StandaloneMirrorPuConfigurer mirrorPu(String puXmlPath) {
		return new StandaloneMirrorPuConfigurer(puXmlPath);
	}

	public static StandaloneMirrorPuConfigurer mirrorPu(Resource puConfigResource) {
		return new StandaloneMirrorPuConfigurer(puConfigResource);
	}

	public static StandaloneMirrorPuConfigurer mirrorPu(Class<?> mirrorConfig) {
		return mirrorPu(createPuXmlResource(mirrorConfig));
	}

	public static class StandalonePartitionedPuConfigurer extends AbstractPartitionedPuConfigurer<StandalonePartitionedPuConfigurer, StandaloneRunningPu> {
		public StandalonePartitionedPuConfigurer(String puXmlPath) {
			super(puXmlPath);
		}

		public StandalonePartitionedPuConfigurer(Resource puConfigResource) {
			super(puConfigResource);
		}

		@Override
		public StandaloneRunningPu configure() {
			return new StandaloneRunningPu(createPuRunner());
		}
	}

	public static class StandaloneMirrorPuConfigurer extends AbstractMirrorPuConfigurer<StandaloneMirrorPuConfigurer, StandaloneRunningPu> {
		public StandaloneMirrorPuConfigurer(String puXmlPath) {
			super(puXmlPath);
		}

		public StandaloneMirrorPuConfigurer(Resource puConfigResource) {
			super(puConfigResource);
		}

		@Override
		public StandaloneRunningPu configure() {
			return new StandaloneRunningPu(createPuRunner());
		}
	}

}
