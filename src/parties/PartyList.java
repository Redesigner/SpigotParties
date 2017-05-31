package parties;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class PartyList {
	
	private ArrayList<Party> parties;
	private JavaPlugin root;
	
	public PartyList(JavaPlugin plugin){
		root = plugin;
		parties = new ArrayList<Party>();
	}
	
	public ArrayList<Party> getParties(){
		return parties;
	}
	
	public boolean register(Party party){
		if(!parties.contains(party)){
			parties.add(party);
			updateOwner(party);
			return true;
		}
		return false;
	}
	
	public void remove(Party party){
		parties.remove(party);
		root.getConfig().set("parties."+party.name, null);
		root.saveConfig();
	}
	
	public Party getParty(String partyname){
		for(Party party:parties){
			if(party.name.equals(partyname)){
				return party;
			}
		}
		return null;
	}
	
	public boolean partyExists(String partyname){
		for(Party party:parties){
			if(party.name.equals(partyname)){
				return true;
			}
		}
		return false;
	}
	
	public boolean inAnyParty(String player){
		for(Party party:parties){
			if(party.inParty(player)){
				return true;
			}
		}
		return false;
	}
	
	public Party getUserParty(String player){
		for(Party party:parties){
			if(party.inParty(player)){
				return party;
			}
		}
		return null;
	}
	
	
	public void deleteAllTrackers(){
		for(Party party:parties){
			party.clearTrackers();
		}
	}
	
	public void updateScoreboards(){
		for(Party party:parties){
			party.updateScoreboard();
		}
	}
	
	public void updateTrackers(){
		for(Party party:parties){
			party.updateTrackers();
		}
	}
	
	public void updateOwner(Party party){
		root.getConfig().set("parties." + party.name + ".owner",
				party.getOwner());
	}
	
	public void updateColor(Party party){
		root.getConfig().set("parties." + party.name + ".color",
				party.getColor().name());
		root.saveConfig();
	}
	
	public void updateMembers(Party party){
		String[] players = new String[party.Members.size()];
		for(int i = 0; i < party.Members.size(); i++){
			players[i] = party.Members.get(i).toString();			
		}
		root.getConfig().set("parties." + party.name + ".members",
				Arrays.asList(players));
	}
	
	public void loadFile(){
		System.out.println("[PartyManager] Loading parties...");
		Configuration config = root.getConfig();
		ConfigurationSection partiesConfig = config.getConfigurationSection("parties");
		for(String partyname:partiesConfig.getKeys(false)){
			System.out.println("[PartyManager]" + partyname + " loaded");
			Party newParty = new Party(partyname, this, root);
			newParty.loadMembers(config.getStringList("parties." + partyname +".members"));
			newParty.setOwner(config.getString("parties." + partyname + ".owner"));
			newParty.setColor(config.getString("parties." + partyname + ".color"));
			register(newParty);
		}
	}
}
