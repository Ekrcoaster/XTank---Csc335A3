# Temp stuff
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
