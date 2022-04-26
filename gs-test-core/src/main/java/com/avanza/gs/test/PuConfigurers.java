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

public class PuConfigurers {

	public static AbstractPartitionedPuConfigurer partitionedPu(String puXmlPath) {
		return new AbstractPartitionedPuConfigurer(puXmlPath);
	}

	public static AbstractPartitionedPuConfigurer partitionedPu(Resource puConfigResource) {
		return new AbstractPartitionedPuConfigurer(puConfigResource);
	}

	public static AbstractPartitionedPuConfigurer partitionedPu(Class<?> puConfig) {
		return partitionedPu(createPuXmlResource(puConfig));
	}

	public static AbstractMirrorPuConfigurer mirrorPu(String puXmlPath) {
		return new AbstractMirrorPuConfigurer(puXmlPath);
	}

	public static AbstractMirrorPuConfigurer mirrorPu(Resource puConfigResource) {
		return new AbstractMirrorPuConfigurer(puConfigResource);
	}

	public static AbstractMirrorPuConfigurer mirrorPu(Class<?> mirrorConfig) {
		return mirrorPu(createPuXmlResource(mirrorConfig));
	}

}
