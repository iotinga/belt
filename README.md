# Introduction

Belt is the Iotinga library for microservices systems written in Java. The goal of Belt is to improve code reuse providing a lightweight architecture structure and a common set of interfaces to help developers on effective usage of its architecture.

Reuse is not intented only at software-level between projects, but at use-case level. For example, supporting the creation of customer service tools reusing the same code of the production microservices.

# Basic Concepts

Belt's architecture is based on a few concepts: decoupling, composability, distribution. All of them are part of the basic Belt building-brick, the Gadget. Writing software in Gadgets allows the developer to inherit Belt's features and properties.

> Belt is not a framework. This means that creating a good architecture is still a Developer's duty.

## What is a Gadget

A Belt's Gadget is, like a Batman's belt gadget, a specific-purpose tool that can be used into multiple belts, even combined with other gadgets. De-facto, a Gadget is a Dependency Injection `Module` (we use Google's Guice as framework) with some additional attributes, like a name, which provides an implementation for the `GadgetCommandExecutor` interface. The `GadgetCommandExecutor`'s `public CompletableFuture<Integer> submit(C command)` method has to be considered as the `public static void main(String[] args);` method in a classic Java software. As the logging features is handled through a specific slf4j belt endpoint, the use of a `GadgetSink` endpoint instead of `System.out` is on 

## What is a Belt

A Belt is a way to run one or multiple Gadget's. De-facto, each Belt is a different way to make three operations before the `public CompletableFuture<Integer> submit(C command)` method is called:

1. Collect basic configuration as a Properties object;
2. Collect user input as a Command object;
3. Build the `GadgetCommandExecutor` dependency injector as described from the Gadget;

> The operations order can be changed from the Belt definition

# The Cli Belt

The Cli Belt is intented to make the Gadget usable through a command line interface, suitable for advanced users or for Iotinga's Customer Service agents.

> At this time, the Cli Belt is not interactive

The Cli Belt loads Properties from a belt.properties file. Passing the -p option to the command, it is possible to specify a different filename and a different path for the file to be used as configuration. Another feature is the -h option which allows to print the command usage guide.

## Lifecycle

1. tries to build the command object parsing the `string[] args` passed from the user;
2. sets the path of the properies file (if needed, using the default instead);
3. asks the Gadget to build the `GadgetCommandExectuor`'s modules passing properties and command objects;
4. obtains the `GadgetCommandExecutor` instance from the new child injector;
5. invokes the `public CompletableFuture<Integer> submit(C command)`
6. `System.exit(result.join());` on the submit method result.

# The Runtime Belt

The Runtime Belt is intended to run multiple Gadgets from the same process. It is useful for Docker microservices use-cases, allowing to decide what gadgets has to run into the docker even after the docker image creation.

> As for bauer and slf4j limitations, all the Gadgets will use the same bauer and slf4j endpoints

The Runtime Belt by default loads properties from the runtime environment variables using the Dotenv library.

It's possible to override the configuration load behavior with these two system properties
(that can be defined with `-Dbelt.x.y=value` Java option):

- `belt.config.loader`: configuration loader, currently supported are `dotenv` (default) and `properties`
- `belt.config.path`: override the configuration file (default: `belt.properties` in the current directory). Only relevant for the `properties` configuration loader.

Example: `java -Dbelt.config.loader=properties -Dbelt.config.path=/etc/mysoftware.properties belt.jar [...]`

It isn't possible to pass any other property or command to the Gadget. It passes a `null` command to the submit method.

## Lifecycle

The lifecycle is a little more complex as it must create and run multiple gadgets, but we can solve this complexity splitting the lifecycle description in three phases: initialization, submission, shutdown

### Initialization

1. tries to build the Runtime belt command object parsing the `string[] args` passed from the user;
2. asks the Runtime Gadget to build the `GadgetCommandExectuor`'s modules passing properties and command objects;
3. obtains the `GadgetCommandExecutor` instance from the new child injector; The module looks at the command to choose what kind of `CompletionService` as to be created to obtain a sequential or parallel execution of Gadgets;
4. invokes the `public CompletableFuture<Integer> submit(C command)` on the Runtime Gadget;
5. `System.exit(result.join());` on the submit method result.

At this point, the main process is waiting that the Runtime stops to terminate. The runtime is now in the submission phase. The command execution iterates on each Gadget canonical name passed and:

1. tries to build the Gadget module using reflection from the runtime's `GadgetCommandExectuor`'s dependency injector. This means that the gadget inherits the `PropertiesProvider` of the Runtime Gadget.
2. asks the Gadget to build the `GadgetCommandExectuor`'s modules passing properties and null command;
3. obtains the `GadgetCommandExecutor` instance from the new child injector;
4. wraps into a Callable the `public CompletableFuture<Integer> submit(C command)` invocation on the Runtime Gadget passing null and waiting for the result;
5. sends the Callable to the Runtime Belt's `CompletionService`.

At this point, if any (important) error occours or if all the Gadget command execution are completed the runtime enters the third phase, shutdown:

1. tries to gracefully shutdown all the running Gadgets
2. waits for 5 seconds for gracefull shutdown
3. terminates with 0 in any case

> Using the ignore option of the Runtime Belt command it is possible to decide if an inner Gadget Error leads to an overall Runtime termination (default behaviour) or if Runtime shall keep up and running the other Gadgets.

# Belt e Bauer

To correcly initialize Bauer we suggest to use the `TopicFactory.getAsModule(Properties properties)` into the Gadget's `public Module[] buildExecutorModules(Properties properties, EntityGadgetCommand command)` method implementation. This allows to be sure you are using the correct properties object for bauer setup.
