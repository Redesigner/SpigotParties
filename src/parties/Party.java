package parties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public class Party {

	
	public ArrayList<String> Members;
	public String name;
	private String owner;
	private PartyList partyList;
	private ChatColor partyColor;
	private ArrayList<TrackerStand> trackers;
	private ArrayList<Player> onlinePlayers;
	
	// A team for using the scoreboard built in features
	private Team team;
	private Objective objective;
	public Scoreboard scoreboard;
	private JavaPlugin root;
	
	public Party(String namearg, PartyList partylist, JavaPlugin root){
		if(namearg.length()>16){
			namearg = namearg.substring(0,15);
		}
		this.root = root;
		name = namearg;
		partyColor = ChatColor.WHITE;
		partyList = partylist;
		Members = new ArrayList<String>();
		trackers = new ArrayList<TrackerStand>();
		onlinePlayers = new ArrayList<Player>();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		team = Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam(name);
		team.setCanSeeFriendlyInvisibles(true);
		team.setAllowFriendlyFire(false);
		Objective health = scoreboard.registerNewObjective("healthParty", "health");
		health.setDisplayName("Health ");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		Objective levels = scoreboard.registerNewObjective("levelParty", "level");
		levels.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		objective = scoreboard.registerNewObjective("level", "dummy");
		objective.setDisplayName(partyColor + name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		updateScoreboard();
	}

	public boolean setColor(String colorName){
		try{
			partyColor = ChatColor.valueOf(colorName);
			for(Player player:onlinePlayers){
				if(player.isOnline()){
					player.setPlayerListName(partyColor+player.getName());
				}
			}
		}
		catch(Exception e){
			System.out.println("[PartiesManager]Attempt to use an invalid color");
			return false;
		}
		partyList.updateColor(this);
		return true;
	}
	
	
	public ArrayList<Player> getOnlinePlayers(){
		return onlinePlayers;
	}
	
	public void login(Player player){
		onlinePlayers.add(player);
		player.setPlayerListName(partyColor + player.getName());
		player.setScoreboard(scoreboard);
		createTrackersOf(player);
		createTrackersFor(player);
	}
	
	public void logout(Player player){
		onlinePlayers.remove(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		player.setDisplayName(player.getName());
		System.out.println("Player has logged out of party " + name);
		System.out.println(onlinePlayers.toString());
	}
	
	
	public void updateScoreboard(){
		Objective objectiveUpdate = null;
		if(objective.getName().equals("level")){
			objectiveUpdate = scoreboard.registerNewObjective("levelBuffer", "dummy");
		}
		else if(objective.getName().equals("levelBuffer")){
			objectiveUpdate = scoreboard.registerNewObjective("level", "dummy");
		}
		objectiveUpdate.setDisplayName(partyColor + name);
		objectiveUpdate.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.unregister();
		objective = objectiveUpdate;
		//System.out.println(onlinePlayers.toString());
		for(Player player:getOnlinePlayers()){
			if(!isOwner(player.getName())){
				objective.getScore(player.getName() + "  Lv.").setScore(player.getLevel());
			}
			else{
				objective.getScore("\u2605"+owner+"\u2605  Lv.").setScore(Bukkit.getPlayer(owner).getLevel());
			}
		}

	}
	
	public void createTrackersFor(Player owner){
		for(Player target:getOnlinePlayers()){
			if(!owner.getName().equals(target.getName())){
				TrackerStand newTracker = new TrackerStand(owner, target, root);
				trackers.add(newTracker);
			}
		}
	}
	
	public void createTrackersOf(Player target){
		for(Player owner:getOnlinePlayers()){
			if(!owner.getName().equals(target.getName())){
				TrackerStand newTracker = new TrackerStand(owner, target, root);
				trackers.add(newTracker);
			}
		}
	}
	
	public void updateTrackers(){
		Iterator<TrackerStand> iterator = trackers.iterator();
		while(iterator.hasNext()){
			TrackerStand tracker = iterator.next();
			if(!tracker.updatePosition(partyColor)){
				tracker.delete();
				iterator.remove();
			}
			else if(!inParty(tracker.target.getName()) || !inParty(tracker.owner.getName())){
				//System.out.println("deleting tracker");
				tracker.delete();
				iterator.remove();
			}
		}
	}
	
	public void clearTrackers(){
		Iterator<TrackerStand> iterator = trackers.iterator();
		while(iterator.hasNext()){
			iterator.next().delete();
			iterator.remove();
		}
	}
	
	public ChatColor getColor(){
		return partyColor;
	}
	
	@SuppressWarnings("deprecation")
	public boolean addMember(String player){
		if(!partyList.inAnyParty(player)){
			Members.add(player);
			team.addPlayer(Bukkit.getOfflinePlayer(player));
			partyList.updateMembers(this);
			try{
				Bukkit.getPlayer(player).isOnline();
				login(Bukkit.getPlayer(player));
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void removeMember(String player){
		if(inParty(player)){
			Members.remove(player);
			team.removePlayer(Bukkit.getOfflinePlayer(player));
			try{
				Bukkit.getPlayer(player).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}catch(Exception e){}
			partyList.updateMembers(this);
			if(isOwner(player)){
				Iterator<String> iterator = Members.iterator();
				while(iterator.hasNext()){
					String playerName = iterator.next();
					if(!isOwner(playerName)){
						if(Bukkit.getPlayer(playerName).isOnline()){
							logout(Bukkit.getPlayer(playerName));
						}
						iterator.remove();;
					}
				}
				Members.remove(player);
				partyList.remove(this);
			}
		}
	}
	
	public void setOwner(String owner){
		this.owner = owner;
		partyList.updateOwner(this);
	}
	
	public String getOwner(){
		return owner;
	}
	
	public boolean isOwner(String user){
		return(user.equals(owner));
	}
	
	public void message(String string, Player player){
		for(String recipient: Members){
			try{
				if(isOwner(player.getName())){
					Bukkit.getPlayer(recipient).sendMessage(partyColor+"<\u2605" + player.getName() +"\u2605>* " + ChatColor.WHITE + string);
				}
				else{
					Bukkit.getPlayer(recipient).sendMessage(partyColor+"<" + player.getName() +">* " + ChatColor.WHITE + string);
				}
			}
			catch(Exception e){}
		}
	}
	
	public void sendMessage(String string){
		for(String recipient: Members){
			try{
				Bukkit.getPlayer(recipient).sendMessage(partyColor+"["+name+"]"+ ChatColor.WHITE + string);
			}
			catch(Exception e){}
		}
	}
	
	public boolean inParty(String player){
		return Members.contains(player);
	}
	
	public void loadMembers(List<String> Players){
		for(String player: Players){
			addMember(player);
		}
	}
	
	public void rename(String name){
		if(name.length()>16){
			name = name.substring(0,15);
		}
		partyList.remove(this);
		this.name = name;
		partyList.register(this);
	}
}
