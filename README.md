# NewMazeOblig

The purpose of this project is to have n different clients traversing through a unique maze, solving it in the end. When it is solved the client moves to starting possition and restarts the maze. The clients always know each others position, and updates an applet continuously. This is done having clients connecting to a server and passing data. The server then collects all this data and periodically sends the data back to each registered client. Project is done using java-rmi technology.

Project is part of a university course in distributed systems, fall 2017. Methods for creating, rendering and solving the maze was provided at start. Methods for dealing with communication to and from server were made by authors.

## Installation

1. Run /src/mazeoblig/RMIServer class in your IDE (Project was made using Intellij). This initializes the server and listens for connections on port 9000
2. Run /src/mazeoblig/Maze. This will create 50 clients, assign them a random position, and start traversing through the maze
3. To add more clients, simply launch another Maze. Alternatively, modfiy the CLIENTS_TO_CREATE property (50 at default) 

## Known problems

* To reduce workload, only one (randomly assigned) client draws the maze. If this client for some reason disconnects, maze rendering will stop   
