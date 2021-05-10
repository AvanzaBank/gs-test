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
package com.avanza.gs.test.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.mockito.InOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

class ResourceExtensionTest {
    // the individual resources are all static, so they can be used in TestClass
    private static final ResourceExtension staticResource1 = spy(new TestResourceExtension());
    private static final ResourceExtension staticResource2 = spy(new TestResourceExtension());
    private static final ResourceExtension instanceResource1 = spy(new TestResourceExtension());
    private static final ResourceExtension instanceResource2 = spy(new TestResourceExtension());

    static class TestClass {

        @RegisterExtension
        static Extension staticResources = staticResource1.andThen(staticResource2);

        @RegisterExtension
        Extension instanceResources = instanceResource1.andThen(instanceResource2);

        @Test
        void firstTest() {
            // intentionally empty
        }

        @Test
        void secondTest() {
            // intentionally empty
        }

    }

    @Test
    void shouldCallBeforeAndAfterInTheCorrectOrder() throws Exception {
        InOrder order = inOrder(staticResource1, staticResource2, instanceResource1, instanceResource2);

        EngineExecutionResults results = EngineTestKit.engine("junit-jupiter")
                                                      .selectors(selectClass(TestClass.class))
                                                      .execute();

        // there are two tests, they should both succeed
        assertThat(results.testEvents().succeeded().list(), hasSize(2));

        // BeforeAll
        order.verify(staticResource1).before();
        order.verify(staticResource2).before();

        // BeforeEach - first test
        order.verify(instanceResource1).before();
        order.verify(instanceResource2).before();

        // AfterEach - first test
        order.verify(instanceResource2).after();
        order.verify(instanceResource1).after();

        // BeforeEach - second test
        order.verify(instanceResource1).before();
        order.verify(instanceResource2).before();

        // AfterEach - second test
        order.verify(instanceResource2).after();
        order.verify(instanceResource1).after();

        // AfterAll
        order.verify(staticResource2).after();
        order.verify(staticResource1).after();
    }

    private static class TestResourceExtension implements ResourceExtension {

        @Override
        public void before() {
            // intentionally empty
        }

        @Override
        public void after() {
            // intentionally empty
        }

    }

}
