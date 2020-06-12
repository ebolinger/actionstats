# ![rocket](rocket.png) ActionStats Library

### Concurrent Action Statistics

# Overview

The ActionStats library is useful for reporting the average time for a set of actions.
Multiple threads can freely report activity in parallel without contention for underlying resources.
The only project dependencies are on Java 1.8 JDK and Maven.  Ensure those are preinstalled and available.


# Getting Started

This library is packaged as a JAR, suitable for inclusion in your own projects.  You can build a local copy using Maven:

```
ActionStats> mvn package
```

This command runs all of the tests & produces the JAR `target/actionstats-1.0.jar`.


# API

The API consists of 2 methods, captured in the `ActionStats` interface:

```
public interface ActionStats {
    void addAction(String json) throws IOException;
    String getStats();
}
```

## addAction(String json)
This method accepts a JSON string for an object with two properties: `action` & `time`.
* It throws an exception if the JSON is not properly formatted.
* Negative values for `time` are not allowed.
* The `action` can be any non-empty String.

## getStats()
This method returns a JSON report of the average times for all actions.  This can be called multiple times or concurrently.


# Use Cases and Examples

In your project, include a dependency on `dev.bolinger.actionstats` and copy the JAR.
(TODO: Publish to Maven Central so developers can pull from there!)

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

- Report issues and bugs directly in [this GitHub project](https://github.com/ebolinger/actionstats/issues).


# License

The project is provided as-is without an warranty or license.

