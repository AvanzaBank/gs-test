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

import org.springframework.context.ApplicationContext;

import java.util.Properties;

public abstract class CommonMirrorPuConfigurer<T extends CommonMirrorPuConfigurer<T>> {

    protected final String puXmlPath;
    protected Properties properties = new Properties();
    protected ApplicationContext parentContext;
    protected String lookupGroupName = JVMGlobalLus.getLookupGroupName();

    protected CommonMirrorPuConfigurer(String puXmlPath) {
        this.puXmlPath = puXmlPath;
    }

    public T contextProperty(String propertyName, String propertyValue) {
        this.properties.setProperty(propertyName, propertyValue);
        return me();
    }

    public T parentContext(ApplicationContext parentContext) {
        this.parentContext = parentContext;
        return me();
    }

    public T lookupGroup(String group) {
        this.lookupGroupName = group;
        return me();
    }

	@SuppressWarnings("unchecked")
		private T me() {
			return (T) this;
		}

}
