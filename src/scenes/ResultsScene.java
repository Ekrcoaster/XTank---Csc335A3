package scenes;

import java.util.ArrayList;
import java.util.Collections;

import battle.tanks.ArchievedTank;
import network.Client;
import network.Message;
import network.NetworkListener;
import network.Server;
import ui.ResultsSceneUI;
import ui.WindowManager;

public class ResultsScene extends Scene implements NetworkListener {

	ResultsSceneUI ui;
	String mapName;
	String playerName;
	public ArrayList<ArchievedTank> archievedTanks = new ArrayList<ArchievedTank>();
	
	public ResultsScene(String mapName, String playerName) {
		this.mapName = mapName;
		this.playerName = playerName;
	}
	
	@Override
	public void init() {
		ui = new ResultsSceneUI(this);
		WindowManager.setPanel(ui);
		
		if(Client.client != null) {
			Client.client.addListener(this);
		} else if(Server.server != null) {
			Server.server.addListener(this);
		}
	}


	@Override
	public void onMessage(Message message) {
		if(message.is("results")) {
			for(int i = 0; i < message.args.size(); i += 4) {
				System.out.println(i + " " + message.getArg(i));
				archievedTanks.add(new ArchievedTank(
					message.getArg(i),
					message.getArg(i+1),
					message.doubleArg(i+2),
					message.getArg(i+3)
				));
			}
			Collections.sort(archievedTanks);
			
			ui.update();
		}
		
		if(message.is("clientExit") || message.is("aClientExited")) {
			String clientID = message.fromID == null ? message.getArg(0) : message.fromID;
			int index = -1;
			for(int i = 0; i < archievedTanks.size(); i++) {
				if(archievedTanks.get(index).getId().equals(clientID))
					index = i;
			}
			
			if(index > -1) {
				ui.removePlayer(index);
				archievedTanks.remove(index);
			}
		}
		
		if(message.is("start") && message.getArg(0).equals("join"))
			SceneManager.setScene(new JoinScene(Client.client != null, Server.server != null, playerName, mapName));
	}
	
	void requestLeave() {
		if(Server.server != null) {
			Server.server.close();
		}
		if(Client.client != null) {
			Client.client.close();
		}
	}
	
	public void playAgain() {
		SceneManager.setScene(new JoinScene(Client.client != null, Server.server != null, Client.client == null ? null : Client.client.getName(), mapName));
		Server.server.sendMessage("start join");
	}
	
	public void toTitle() {
		requestLeave();
		SceneManager.setScene(new TitleScene());
	}
	
	public void exitGame() {
		requestLeave();
		System.exit(0);
	}

	@Override
	public void onSentMessage(Message message) { }
	@Override
	public void exit() { }

}
