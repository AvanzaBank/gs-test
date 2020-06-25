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

import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Stream;

import com.gigaspaces.security.AccessDeniedException;
import com.gigaspaces.security.Authentication;
import com.gigaspaces.security.AuthenticationException;
import com.gigaspaces.security.Authority;
import com.gigaspaces.security.SecurityException;
import com.gigaspaces.security.SecurityManager;
import com.gigaspaces.security.authorities.SpaceAuthority;
import com.gigaspaces.security.directory.DirectoryManager;
import com.gigaspaces.security.directory.User;
import com.gigaspaces.security.directory.UserDetails;

public class SecurityManagerForTests implements SecurityManager, Serializable {
	private static final Authority[] TEST_AUTHORITIES = Stream.of(SpaceAuthority.SpacePrivilege.values())
			.map(SpaceAuthority::new)
			.toArray(Authority[]::new);

	@Override
	public void init(Properties properties) throws SecurityException { }

	@Override
	public void close() { }

	@Override
	public DirectoryManager createDirectoryManager(UserDetails userDetails) throws AuthenticationException, AccessDeniedException {
		throw new UnsupportedOperationException("DirectoryManager is not supported by SecurityManagerForTests");
	}

	@Override
	public Authentication authenticate(UserDetails userDetails) throws AuthenticationException {
		// During tests, we allow any username & password
		return new Authentication(new User(userDetails.getUsername(), userDetails.getPassword(), TEST_AUTHORITIES));
	}
}
