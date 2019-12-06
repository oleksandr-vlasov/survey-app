# Survey Application

Simple application that allows users to pass short survey.

## Structure

| Module | Description |
|---| --- |
| **survey-app** | Main application module. Includes other modules and configures application. |
| **survey-core** | Core functionality that can be shared across other modules. |
| **survey-networking** | Core networking module, contains interfaces and models for network communication. |
| **survey-networking-retrofit** | Networking module implementation using Retrofit. |
| **survey-testing** | Core testing module that can share base classes for unit testing across other modules. |

## Testing

To execute unit tests run the command:
```
gradlew clean tDUT
```

To execute instrumentation tests run the command (doesn't work at this moment, use Android Studio instead):
```
gradlew clean cDAT
```