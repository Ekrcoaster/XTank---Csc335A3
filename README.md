
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
