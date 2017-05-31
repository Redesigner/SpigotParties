package parties;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class partychat extends JavaPlugin{

	static PartyList partyList;
	static MessageLoader messageLoader;
	static InviteList inviteList;
	
	@Override
	public void onEnable() {
		partyList = new PartyList(this);
		messageLoader = new MessageLoader(this);
		inviteList = new InviteList(partyList);
		getCommand("party").setExecutor(new PartyCommand(partyList, inviteList));
		getCommand("p").setExecutor(new PartyMessage(partyList));
	    loadConfiguration();
	    partyList.loadFile();
	    System.out.println("[PartiesManager] Running...");    
	    
	    
	    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
	    	public void run(){
	    		partyList.updateScoreboards();
	    		partyList.updateTrackers();
	    		inviteList.update();
	    	}
	    }, 0,1L);
	    
	    getServer().getPluginManager().registerEvents(new LoginEvent(partyList), this);
	}
	
	@Override
	public void onDisable() {
		partyList.deleteAllTrackers();
	    saveConfig();
	}
	public void loadConfiguration(){
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	}
}
