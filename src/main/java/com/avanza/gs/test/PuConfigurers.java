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

import org.springframework.core.io.Resource;

public class PuConfigurers {

	public static PartitionedPuConfigurer partitionedPu(String puXmlPath) {
		return new PartitionedPuConfigurer(puXmlPath);
	}

	public static PartitionedPuConfigurer partitionedPu(Class<?> puConfig) {
		return partitionedPu(createPuXmlConf(puConfig).toUri().toString());
	}

	public static PartitionedPuConfigurer partitionedPu(Resource puConfigResource) {
		return new PartitionedPuConfigurer(puConfigResource);
	}

	public static MirrorPuConfigurer mirrorPu(String puXmlPath) {
		return new MirrorPuConfigurer(puXmlPath);
	}

	public static MirrorPuConfigurer mirrorPu(Class<?> mirrorConfig) {
		return mirrorPu(createPuXmlConf(mirrorConfig).toUri().toString());
	}

	public static MirrorPuConfigurer mirrorPu(Resource puConfigResource) {
		return new MirrorPuConfigurer(puConfigResource);
	}
}
