# GS-Test
[![][travis img]][travis]
[![][maven img]][maven]
[![][license img]][license]
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/AvanzaBank/gs-test.svg)](http://isitmaintained.com/project/AvanzaBank/gs-test "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/AvanzaBank/gs-test.svg)](http://isitmaintained.com/project/AvanzaBank/gs-test "Percentage of issues still open")

The GS-Test library contains utilities designed to simplify testing of applications implemented using GigaSpaces.

#### Running an embedded Processing Unit
The `PuConfigurers` class contains factory methods for builders for different types of processing units (partitioned pu, mirror pu). Those can be used to create an embedded processing unit (`RunningPu`). The `RunningPu` extends the `org.junit.rules.TestRule` interface which makes it easy to run a processing unit "around" either an entire test class using `@ClassRule`, or around each test case using `@Rule`.

##### Example: Using @Rule and RunningPu to start/stop a pu around each test case
```java
class FruitTest {
  @Rule
  public RunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
                                   .configure();
                                   
  // Test cases against fruitPu
}
```

##### Example: Starting/stopping a Pu explicitly
```java
class FruitTest {

  RunningPu fruitPu = PuConfigurers.partitionedPu("classpath:/fruit-pu.xml")
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

## Maven

GS-Test packed as a single jar file. Maven users can get GS-Test using the following coordinates:

```xml
<dependency>
  <groupId>com.avanza.gs</groupId>
  <artifactId>gs-test</artifactId>
  <version>1.0.0</version>
</dependency>
``` 

## Previous verisons

[v0.1.x](https://github.com/AvanzaBank/gs-test/tree/v0.1.x) - Based on GigaSpaces 10.1.1 and Java 8

## License
The GS-Test library is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

[travis]:https://travis-ci.org/AvanzaBank/gs-test
[travis img]:https://api.travis-ci.org/AvanzaBank/gs-test.svg

[release]:https://github.com/avanzabank/gs-test/releases
[release img]:https://img.shields.io/github/release/avanzabank/gs-test.svg

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

[maven]:http://search.maven.org/#search|gav|1|g:"com.avanza.gs"
[maven img]:https://maven-badges.herokuapp.com/maven-central/com.avanza.gs/gs-test/badge.svg
