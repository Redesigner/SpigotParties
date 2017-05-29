package parties;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class partychat extends JavaPlugin{

	static PartyList partyList;
	static MessageLoader messageLoader;
	
	@Override
	public void onEnable() {
		partyList = new PartyList(this);
		messageLoader = new MessageLoader(this);
		getCommand("party").setExecutor(new PartyCommand(partyList));
		getCommand("p").setExecutor(new PartyMessage(partyList));
	    loadConfiguration();
	    partyList.loadFile();
	    System.out.print("Parties enabled!");
	    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
	    	public void run(){
	    		partyList.updateScoreboards();
	    	}
	    }, 11,11);
	}
	
	public void onPlayerJoin(){
		
	}
	@Override
	public void onDisable() {
		
	}
	public void loadConfiguration(){
	     getConfig().options().copyDefaults(true);
	     saveConfig();
	}
}
