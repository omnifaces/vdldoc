# Build

Vdldoc requires Java 17 to build.

```bash
mvn clean install
```

This also runs the unit tests.
There are no integration tests.

# Run tests

```bash
mvn clean test
```

To run a single test class:

```bash
mvn clean test -Dtest=MainTest
```
