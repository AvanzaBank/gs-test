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

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeJars;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.avanza.gs.test", importOptions = {DoNotIncludeTests.class, DoNotIncludeJars.class})
class CommonPackageArchitectureTest {

    @ArchTest
    static final ArchRule classesInCommonPackageShouldNotDependOnJUnit =
            noClasses()
                    .that().resideInAPackage("com.avanza.gs.test")
                    .should()
                    .dependOnClassesThat().resideInAPackage("org.junit.*");

    @ArchTest
    static final ArchRule classesInCommonPackageShouldNotDependOnJUnitSpecificPackagesUnlessDeprecated =
            noClasses()
                    .that().resideInAPackage("com.avanza.gs.test").and().areNotAnnotatedWith(Deprecated.class)
                    .should()
                    .dependOnClassesThat().resideInAnyPackage("com.avanza.gs.test.junit4", "com.avanza.gs.test.junit5");
}
