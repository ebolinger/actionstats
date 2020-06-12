# ![rocket](rocket.png) ActionStats Library

### Concurrent Action Statistics

# Overview

The ActionStats java library is used to measure the average time for a set of actions.
Multiple threads can freely report activity in parallel without contention for underlying resources.
The only project dependencies are Maven and Java JDK version 1.8 or higher.  Ensure those are preinstalled and available.

# Getting Started

This library is packaged as a JAR, suitable for inclusion in your own projects.  You can build a local copy using Maven:

```
actionstats> mvn package
```

This command runs all of the tests & produces the JAR `target/actionstats-1.0.jar`.


Install this JAR to your local Maven repository to make it accessible to other projects:
```
actionstats> mvn install
```

Other Maven commands like `clean` or `test` also work as expected.

# API

The API consists of 2 methods, captured in the `ActionStats` interface:

```
public interface ActionStats {
    void addAction(String json) throws IOException;
    String getStats();
}
```

## addAction(String json)
This method accepts a JSON string with two properties: `action` & `time`, for example: `{ "action": "api", "time": 52 }`
* `addAction()` throws an exception if the JSON string is not properly formatted.
* Negative values for `time` are not allowed.
* The `action` can be any non-empty string.

## getStats()
This method returns a JSON report of the average time for each action.  This can be called multiple times or concurrently.  There is no easy way to "reset" the metrics other than creating a new instance of this class.  Some developers consider this a feature...


Note: There are no extra threads running in the background.  All calculations are done using your client threads.

# Use Cases and Examples

In your project, declare a dependency on `dev.bolinger / actionstats / 1.0` that exists in your local Maven repo.
(TODO: Publish to Maven Central so developers can pull from there!)

## Include (Maven)

Including this JAR in a Maven project is very straightforward:
```
<dependency>
    <groupId>dev.bolinger</groupId>
    <artifactId>actionstats</artifactId>
    <version>1.0</version>
</dependency>

```

## Include (Gradle)

Gradle can fetch JARs from a local Maven repository given the right configuration.
Include the following in your `build.gradle` file to pull this JAR from the local Maven repo:

```
repositories {
    mavenLocal()
}

dependencies {
    implementation 'dev.bolinger:actionstats:1.0'
}
```

## Include (SBT)

If you happen to be using SBT, the following works in a `build.sbt` file:
```
libraryDependencies += "dev.bolinger" % "actionstats" % "1.0"

resolvers += Resolver.mavenLocal
```

## Example

After you have the dependency working, it is easy to start measuring activity:

```
import dev.bolinger.actionstats.impl.ConcurrentActionStats;

ActionStats stats = new ConcurrentActionStats();

stats.addAction("{ \"action\":\"jump\", \"time\": 100 }");
stats.addAction("{ \"action\":\"run\", \"time\": 75 }");
stats.addAction("{ \"action\":\"jump\", \"time\": 200 }");

String report = stats.getStats();
```


# Contributing

Contributions to the code, examples, documentation, etc. are very much appreciated.

* Report issues and bugs directly in [this GitHub project](https://github.com/ebolinger/actionstats/issues).


# License

The project is provided as-is without warranty or license.

