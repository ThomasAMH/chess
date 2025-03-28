## Commit Log
1. Added the test files and organized program directory in accordance in with instructions
2. Draft up server facade
3. Wrote first test: register user.
4. Added login and register tests
5. Add logout tests
6. Added create game tests 
7. Added list game tests
8. Completed join game tests
9. Got login ui working; refactored how errors and handled
10. Added login and logout
11. Add game feature added
12. Added list games feature
13. Added join games feature
14. Crated board drawing methods
15. Added observe feature
16. Added labels to chessboard
17. Corrected board labels
18. Moved some folders to shared
19. Refactored server class to have correct imports
20. Refactored ServerFacade to have correct imports
21. Refactored client to have correct imports
22. Refactored server facade test file to have correct imports

## Phases
Design repl loops
* Validator
* Handler

Display help text
* For each phase, spit out help from resource file

Design UI

Server facade
* Prepare all the necessary requests by means of functions
* Return the server's return objects

Tests for Server facade
* Demonstrate functionality for everything... A front end to play tennis with the back end on

Register
Login
List existing games
Create a new game
See a drawn chessboard
* Should have flipped feature too

Play a game
Observe a game
Logout
Exit


## UI Rules
* Focus on the user experience
* Only present things in a way that the user can relate to.

* Nothing in JSON - If you have something in JSON, parse it and then print out only the information you want the user to see.
* No Authtokens and Game IDs - These are important to keep track of but the user should not be aware of these internal variables. See the requirements for the Postlogin UI for what to display instead of Game IDs.
* No HTTP status codes - The user should not be made aware of internal details like this. Ask yourself, apart from 404, when was the last time you saw a status code on a professional website?
* No stack traces - Hackers love these kinds of internal details as they show how your code works. These should never be displayed to users, although logging them to a place users will not find may be useful. Instead of a stack trace, a simple message informing the user an error occurred (and hopefully why the error occurred without too many details) should be sufficient.
* No crashing. If an exception occurs, catch it. A user may mistakenly put in all kinds of bad input and your client should be able to handle bad input without crashing. This includes incorrect number of arguments (too few or too many), wrong types of arguments (a word when the code expects a number, arguments in the wrong order, etc.), and arguments that the server rejects (register with an existing username, login with incorrect username/password, etc.). When you go to pass off, the TA will test for bad inputs, so please test all of these examples on your own first and ensure the program does not crash and provides reasonable error messages when each error occurs.