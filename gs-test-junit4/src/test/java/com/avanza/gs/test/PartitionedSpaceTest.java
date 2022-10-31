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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.space.UrlSpaceFactoryBean;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.j_spaces.core.IJSpace;

@Deprecated
public class PartitionedSpaceTest {

	private static final String SPACE_NAME = "the_space_name";

	@ClassRule
	public static final PartitionedSpace space = new PartitionedSpace(2, SPACE_NAME);

	private GigaSpace clustered;
	private GigaSpace partition1;
	private GigaSpace partition2;

	@Before
	public void setup() {
		clustered = space.createClusteredProxy();
		partition1 = space.primary(1);
		partition2 = space.primary(2);
	}

	@After
	public void cleanSpace() {
		space.clean();
	}

	@Test
	public void writeToPartition1() {
		assertEquals("expected space to be empty before insert", 0, clustered.count(null));

		clustered.write(createMessage("foo", 0));
		assertEquals(1, partition1.count(null));
		assertEquals(0, partition2.count(null));
		assertEquals(1, clustered.count(null));
	}

	@Test
	public void writeToPartition2() {
		assertEquals("expected space to be empty before insert", 0, clustered.count(null));

		clustered.write(createMessage("foo", 1));
		assertEquals(0, partition1.count(null));
		assertEquals(1, partition2.count(null));
		assertEquals(1, clustered.count(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionWhenAskingForNonexistingPrimary1() {
		space.primary(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionWhenAskingForNonexistingPrimary2() {
		space.primary(3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionIfNumberOfPartitionsIsLessThan2() {
		new PartitionedSpace(1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionIfSpaceNameIsEmpty() {
		new PartitionedSpace(1, "");
	}

	@Test
	public void cleansSpace() {
		clustered.write(createMessage("foo", 0));
		clustered.write(createMessage("foo", 1));
		assertEquals(2, clustered.count(null));

		space.clean();
		assertEquals(0, clustered.count(null));
	}

	@Test
	public void spaceName() {
		assertEquals("gigaSpace", partition1.getName());
		assertEquals(SPACE_NAME, space.getName());
	}

	@Test
	public void connectToSpaceWithUrl() {
		UrlSpaceFactoryBean urlSpaceFactoryBean = new UrlSpaceFactoryBean(space.getUrl());
		urlSpaceFactoryBean.afterPropertiesSet();
		IJSpace obj = (IJSpace) urlSpaceFactoryBean.getObject();
		assertThat(obj.getContainerName(), startsWith(SPACE_NAME));
	}

	private Message createMessage(String message, int routingId) {
		Message m = new Message();
		m.setMessage(message);
		m.setRoutingId(routingId);
		return m;
	}

	public static class Message {

		private String message;
		private int routingId;
		private String id;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@SpaceRouting
		public int getRoutingId() {
			return routingId;
		}

		public void setRoutingId(int routingId) {
			this.routingId = routingId;
		}

		public void setId(String id) {
			this.id = id;
		}

		@SpaceId(autoGenerate = true)
		public String getId() {
			return id;
		}

	}

}
