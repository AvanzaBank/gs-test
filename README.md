# GS-Test
[![][build img]][build]
[![][maven img]][maven]
[![][license img]][license]
[![Average time to resolve an issue](https://isitmaintained.com/badge/resolution/AvanzaBank/gs-test.svg)](https://isitmaintained.com/project/AvanzaBank/gs-test "Average time to resolve an issue")
[![Percentage of issues still open](https://isitmaintained.com/badge/open/AvanzaBank/gs-test.svg)](https://isitmaintained.com/project/AvanzaBank/gs-test "Percentage of issues still open")

The GS-Test library contains utilities designed to simplify testing of applications implemented using GigaSpaces.

#### Running an embedded Processing Unit
The `PuConfigurers`/`StandalonePuConfigurers` classes contains factory methods for builders for different types of processing units (partitioned pu, mirror pu).
Those can be used to create an embedded processing unit (`RunningPu`).
The `RunningPu` is available as a `@TestRule` for JUnit4 and as an `Extension` for JUnit5.

##### Example: Using JUnit4 @Rule and RunningPu to start/stop a pu around each test case
```java
class FruitTest {
  @Rule
  public RunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
                                          .configure();
                                   
  // Test cases against fruitPu
}
```

##### Example: Using JUnit5 @RegisterExtension to start/stop a pu around each test case
```java
class FruitTest {
  @RegisterExtension
  public RunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
                                          .configure();
                                   
  // Test cases against fruitPu
}
```

##### Example: Starting/stopping a Pu explicitly, without any test framework
```java
class FruitTest {

  StandaloneRunningPu fruitPu = StandalonePuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
                                                       .configure();
  
  @Before                                 
  public void startFruitPu() {
      fruitPu.start();
  }
  
  @After                                 
  public void stopFruitPu() {
      fruitPu.stop();
  }
                                   
  // Test cases against fruitPu
}

```

## ZooKeeper

Since version `2.1.0` this library comes bundled with ZooKeeper, and switches to using a ZooKeeper based leader selector for GigaSpaces.

In order to run tests without ZooKeeper, run the tests with system property `-Dcom.avanza.gs.test.zookeeper.disable=true`.
In this case, a LUS-based selector will be used. 

## Maven

### Core

The core module that contains standalone configuration, without depending on any testing framework.

```xml
<dependency>
  <groupId>com.avanza.gs</groupId>
  <artifactId>gs-test-core</artifactId>
  <version>2.1.x</version>
</dependency>
``` 

### JUnit4

JUnit4 bindings for running as test rules.

```xml
<dependency>
  <groupId>com.avanza.gs</groupId>
  <artifactId>gs-test-junit4</artifactId>
  <version>2.1.x</version>
</dependency>
``` 

This artifact replaces `com.avanza.gs:gs-test` from pre-`2.1.0` versions of this library.

### JUnit5

JUnit5 extension bindings.

```xml
<dependency>
  <groupId>com.avanza.gs</groupId>
  <artifactId>gs-test-junit5</artifactId>
  <version>2.1.x</version>
</dependency>
```

## Previous versions

| Branch                                                      | Description                           |
|-------------------------------------------------------------|---------------------------------------|
| [v0.1.x](https://github.com/AvanzaBank/gs-test/tree/v0.1.x) | Based on GigaSpaces 10.1.1 and Java 8 |
| [gs14.5](https://github.com/AvanzaBank/gs-test/tree/gs14.5) | Based on GigaSpaces 14.5 and Java 11  |

## License
The GS-Test library is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

[build]:https://github.com/AvanzaBank/gs-test/actions/workflows/build.yml
[build img]:https://github.com/AvanzaBank/gs-test/actions/workflows/build.yml/badge.svg

[release]:https://github.com/avanzabank/gs-test/releases
[release img]:https://img.shields.io/github/release/avanzabank/gs-test.svg

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

[maven]:https://search.maven.org/#search|gav|1|g:"com.avanza.gs"
[maven img]:https://maven-badges.herokuapp.com/maven-central/com.avanza.gs/gs-test/badge.svg
