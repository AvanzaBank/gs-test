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

public final class JVMGlobalLus {

	/**
	 * @deprecated This method has been deprecated, as we have switched to unicast instead of multicast for these tests.
	 *             Use {@link JVMGlobalGigaSpacesManager#getLookupLocator()} instead.
	 *             The new method uses unicast instead of multicast, so instead of setting {@code lookupGroups} in space
	 *             properties, {@code locators} should be set instead.
	 */
	@Deprecated
	public static String getLookupGroupName() {
		throw new UnsupportedOperationException("This method has been removed. See javadoc for how to migrate to the new method.");
	}
}
