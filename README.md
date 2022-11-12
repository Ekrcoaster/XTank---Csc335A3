
# Welcome to XTank!
Run **Boot.java** and then choose how you want to play the game. You can create a server, join a server, or both!

Use the IP and port number shown on the server's screen to connect to the right server, then you can join in the game and choose your tank. Each tank works in a different way and has different functionality. 

After all players have been added and they have chosen their tanks, the server can start the game! Use your arrow keys to move your tanks around, spacebar to shoot. If you were killed, you'll spectate until the end of the game. If you'd like to leave, press L.

After all but 1 player has been killed, the results screen will pop up and from there you can replay, return to title, or exit the game entirely.

Note: I did not hear from my partner at all during this project, it was made completely alone.

### Some design details that may be interesting:
The game is built ontop of a server-client foundation I made, it allows for infinite players and can easily send messages between the server and client. This followers the observer pattern, to quickly and easily handle many clients.

The title screen, join screen, battle screen, and results screen are all different scenes that are handled using the algorithmic pattern. One scene can hold a generic scene, then each one implements overtop of that.

The battle itself creates a game loop that runs in a new thread, it runs 30fps and will update the tanks, then render the screen. Server events are used to set those tank positions during the update stage, then they will be rendered. Movement is handled by each client, the server mearly echos the movement out. But more important things such as bullets and health are completely handled by the server. The bullets on the client side don't do anything, the server runs the calculation. This approach keeps the game running smoothly while keeping it fair. When the game ends, the server will send its results to all clients, and the results screen is swapped to.


