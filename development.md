# Nov 11th
Finished everything, refactored the code so its cleaner, everything has comments, the game is now fully stable. There are some bugs here and there but for the most part it is completely finished!

# Nov 8th
Multiple different type of tanks and bullets have been added, health and stuff for multiplayer has been finished and works properly. You can choose a map on the title screen, and if you are a player, you can choose your tank on the join screen using some cool fancy ui stuff.

Todo still: (not really in order)
- Maybe some sort of like player list to show who is alive
- death screen + restart game?
- gameplay overlays (with info to the player)
- refactor the code
- Make sure everything meets the spec requirement

_-Ethan_

# Nov 7th
I've added the mutliplayer functionality completely! You can now run **boot.java** and you'll be prompted with the game creation screen, then a place to join in, then you can being the game!

The way this works is every server/client has a running instance of the battle, the client will tell the server what changes the player has made, the server will reflect that and tell the rest of the clients what has been made.

Collisions will be calculated on the player's end, its not the most secure, but its the simplest.
Bullet impacts will be calculated on the server, however. Since bullets aren't attached to a single client. Once a tank has been shot, it'll alert all of the clients of the health change.
I haven't actually added these yet, but i will soon.

I'm going to start working on the collision system and maps in a seperate branch. I'll update this when its done.

---
So its done, you can create map files in the maps folder, theres a tutorial in empty.txt
I haven't tested them on multiplayer yet, but it should work perfectly as its all client side.

Tomorow I'll add the interface for selecting maps and other tanks!

Todo still: (not really in order)
- add the UI for chosing tanks and maps (ill do this tomorrow)
- bullet collisions with tanks (i'll do this tomorrow, will just use the collision system)
- Tank health and deaths (prob after the server stuff has been added)
- Different types of tanks and bullets and maps (should be easily, we should do this last)
- Maybe add a choose your tank to the player join waiting list
- Maybe some sort of like player list to show who is alive
- Floating health bars above the tanks in game
- Make sure everything meets the spec requirement

_-Ethan_

# Nov 6th
I've added a lot more server/client backend stuff, you can now add the **NetworkListener** interface to your class, then call **_Client.client.addListener(this);_**  to start listening for client messages, or **_Server.server.addListener(this);_** to start listening for server messages. I've modified how the debug consoles work to follow this model.

I have also created 2 UI scenes, the first is for the title screen. You can run this by launching **SceneManager.java**. It allows you to create a server, join a server, or create AND join the server. You can also change the IP and ports, and your name. 

Once you continue, you'll be taken to the join screen. This shows a list of all current players and once everyone is ready, the server owner can hit start to begin the game. Here players can connect to the server, I'm trying hard to make sure its obvious how to use the UI, any feedback would be super helpful! Pressing the play button does nothing at the moment, but it will take you to the battle screen and the game will begin. I'll probably do this tomorrow, I just have to connect the tank game up.

I'm trying to build the tank game so it can be played singleplayer, which should make testing/debugging super easily, so you dont have to go through the hassle of creating a server and stuff every time.

----
The BTP (battle tank protocal) is the way I'll have the server and client communicate. It works pretty simply:

CommandLabel (arg1) (arg2) (arg3) (arg....)
Then the message is signed by the ID it was sent from (if sent from the server, it is null)

for example, a command could look like:
**join 124uh21 Player1**  - Player 1 has joined, their id is 124uh21

or for some more complex commands:
**playerList**  - Hey server, send me the player list (then the server would respond with...)
**retPlayerList (clientID1) (clientName1) (clientID2) (clientName2)...** - Hey client, here is the player list!

I'll be documenting all of the commands I'm adding and using in **commands.txt** with more detailed descriptions

Things left to do: (not really in order)
- Sync the battle to the server/client setup (ill do this hopefully tomorrow)
- Add collisions to tanks (probably a general collision system that we could reuse)
- Add collisions to bullets
- Tank health and deaths (prob after the server stuff has been added)
- Collider walls and custom maps (im thinking we could use a large text file and we can just build maps out of a large number grid.
- Different types of tanks and bullets and maps (should be easily, we should do this last)
- Maybe add a choose your tank to the player join waiting list
- Maybe some sort of like player list to show who is alive
- Floating health bars and names above the tanks in game
- Make sure everything meets the spec requirement

_-Ethan_
 

# Nov 5th
I've added in a basic UI for the game in the game, you can open it by running **SceneManager.java** which will create the battle scene and will open everything up. There is 1 tank to start and you can use your arrow keys to move around, and use space to shoot.

Scenes are just ways to organize the code and UI, I reckon we would probably have a title screen, a level chooser, a tank chooser, a game over screen, etc. So the scenes let us easily swap out UIs in realtime and change code quickly.

The battle scene uses an extra thread to run the game, it runs at 30fps and will update the tanks, then redraw the screen. The screen is drawn by overriding a JPanel's drawing system, I tried all sorts of things, including SWT, and it didn't work. This is the best solution I've got.

I've yet to add in the UI to the server/client stuff, I'll probably do that soon. It shouldn't be too tricky, I think we will have to seperate the abstract tank class into an abstract (server controlled) tanked class, and an abstract (player controlled) tank class. The player one can prob inhereiht from the server one, it could almost be like a lifeless corpse while the player has all of the cool default functionality or something.

Things left to do: (not really in order)
- Add collisions to tanks
- Add collisions to bullets
- Tank health and deaths (prob after the server stuff has been added)
- Collider walls and custom maps (im thinking we could use a large text file and we can just build maps out of a large number grid.
- Different types of tanks and bullets and maps (should be easily, we should do this last)
- Connect the server stuff to the ui and the client and get all of the positions and stuff synced (I can do this once since I coded the server stuff and the UI)
- The welcome/ choose your player screen
- Maybe some sort of like player list to show who is alive
- Floating health bars and names above the tanks in game
- Make sure everything meets the spec requirement

_-Ethan_

# Nov 3rd
The server api thing I made is all setup, basically it can support infinite players and can send messages from client => server and server => all clients. If a client is started up without a server created, then it will autogenerate the server too.

I've also created a temp debug console that'll open up with the server/client, it will show the message history and will also let you manually send messages back and forth. I imagine thisll be super helpful early on so we can see/debug exactly what is being sent back and forth.

To launch the server only, run **Server.java**
To launch the client+server, run **Client.java**
To launch the client only, change **_clientCreatesServer_** to false in **_Settings.java** and run **Client.java**

You can also change the IP and port in **_Settings.java**.

---
To send a message to the server, call **_sendMessage_** inside of **Client.java**. To send a message to the clients, call **_sendMessage_** inside of **Server.java**

Received messages from the server are called inside of **_messageReceived_** in **Client.java**. The param *from* will be null, this is because the sender will always be the server and a reference would be impossible since the server isn't running on the client's end.

Received messages from any of the clients are called inside of **_messageReceived_** in **Server.java**. The param *from* will be a reference to the sender. You can get the client's ID using the **from.getID();**

All messages are currently strings, but we could possibly do like integers or something, we could figure it out. I think if string->int parsing takes too long we could just send the value.

Both the server and client have static references to themselves, so they can be called anywhere in the code. This is because there should only exist 1 client and/or 1 server per runtime, so a static reference helps make code easier. It can make the code a bit messier, but I think it works for now? idk lol

_-Ethan_
