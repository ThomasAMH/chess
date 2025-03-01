
## Design Principles
- No dumb names
- No repetition

## Roadmap
~~1. Create all parent class files (renaming is OK for later)~~

~~2. Create basic parent class functionality~~
3. Construct server
   1. Return the test page
   2. Set up dummy endpoints
4. Construct DAC's
5. Construct endpoints as follows:

### For each endpoint
1. Plan out each class
2. Create empty classes, handlers, and functions
3. Create JTests
   - What data is needed?
4. Write happy flow
5. Write exception flows
6. Try to break it

## Notes on Testing
_server/src/test/java/service_r
In addition to the HTTP server pass off tests provided in the starter code, you need to write tests that execute directly against your service classes. These tests skip the HTTP server network communication and will help you in the development of your service code for this phase.

Good tests extensively show that we get the expected behavior. This could be asserting that data put into the database is really there, or that a function throws an error when it should. Write a positive and a negative JUNIT test case for each public method on your Service classes, except for Clear which only needs a positive test case. A positive test case is one for which the action happens successfully (e.g., successfully claiming a spot in a game). A negative test case is one for which the operation fails (e.g., trying to claim an already claimed spot).

The service unit tests must directly call the methods on your service classes. They should not use the HTTP server pass off test code that is provided with the starter code.


## Classes, Functions, Responsibilities
_Steal from the specs here!_

### Server
- run()
  - Starts the server
- stop()
  - ...
- _The unit tests will start the server on port is 0_

The starter code provides a simple web browser interface for calling your server endpoints. This is useful for experimentation while you are developing your endpoints. In order for your server to be able to load its web browser interface you need to determine the path where the web directory is located and then tell spark to load static web files from that directory.

    Spark.staticFiles.location("web");

You will want to put the web directory in a src/main/resources directory and make the folder as Resources Root in IntelliJ. This location and distinction makes it so the JVM can file the directory at runtime.



### Data Access Classes
_Save to: server/src/main/java/dataaccess_
- [ ] dataAccess.DataAccessException _error class_
  * The starter code includes a dataAccess.DataAccessException.  This exception should be thrown by data access methods that could fail.  Can be made to be more specific through subclasses.
!! Review the documentation here, as there needs to be inheritance for an "in memory" class and a db class


### Data Classes
_Record classes that belong here: shared/src/main/java/model_

**UserData**

| Field    | Type       |
|----------|------------|
| username |            |
| password | all string |
| email    |            |


**GameData**

| Field         | Type      |
|---------------|-----------|
| gameID        | int       |
| whiteUsername | String    |
| blackUsername | String    |
| gameName      | String    |
| game          | ChessGame |

**AuthData**

| Field     | Type       |
|-----------|------------|
| authToken |            |
| username  | all string |

### Services
_server/src/main/java/service_

#### Results
_Designed based on the json objects returned_

### Handlers

#### Requests
_Designed based on the objects requested_