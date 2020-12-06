# Simple Game via HTTP of Clients With Server

This repository includes two classes which can be started: `GameServer` and `ConnectionSimulation`. The former class 
starts a server which a client can contact to play the game. The latter class simulates a given amount of clients who 
sequentially play a whole round of the game with the server automatically.

## GameServer

To start the class `GamesServer`, you have to provide the port number it should listen to as an argument: 
`java GameServer <port-number>`. The port number could for example be 5000. The server then starts and listens to the 
provided port. The server reacts on HTTP/1.1 GET and POST requests and the connection is not encrypted. The server has 
to be called with `http://<host-address>:<port-number>/http_post.html` to start the game. Each time, a new thread of 
the class `ClientManager` is started.

The client receives a question to guess a number between 1 and 100 which the server chose (for simplicity, the server 
uses a fixed number). After the clients sends the guessed value via a field, it receives a statement whether the guess 
is too high, too low or correct. It can continue its guesses because a client id gets assigned as a cookie to each 
client. This helps the server to identify the past guesses of this client.

## ConnectionSimulation

The class `ConnectionSimulation` needs the two arguments port number and the number of simulated rounds: 
`java ConnectionSimulation <port-number> <number-simulated-rounds>`. The port number specifies to which port the 
simulated clients should connect to. The second argument specifies how many rounds should be played.

The programme connects to the given port and guesses a number. Based on the result, the simulation then tries to 
find a better number until it succeeds. At the end of each round, the number of needed turns to win this round is 
printed on the console. At the end of the whole simulation, the number of the played rounds and the average number of 
turns per round is printed on the console.
