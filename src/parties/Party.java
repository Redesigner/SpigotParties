package parties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class Party {

	
	public ArrayList<String> Members;
	public String name;
	private String owner;
	private PartyList partyList;
	private ChatColor partyColor;
	private ArrayList<TrackerStand> trackers;
	
	// A team for using the scoreboard built in features
	private Team team;
	private Objective objective;
	private Scoreboard scoreboard;
	
	public Party(String namearg, PartyList partylist){
		if(namearg.length()>16){
			namearg = namearg.substring(0,15);
		}
		name = namearg;
		partyColor = ChatColor.WHITE;
		partyList = partylist;
		Members = new ArrayList<String>();
		trackers = new ArrayList<TrackerStand>();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		team = Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam(name);
		team.setCanSeeFriendlyInvisibles(true);
		team.setAllowFriendlyFire(false);
		objective = scoreboard.registerNewObjective("test", "test");
		objective.setDisplayName(partyColor + name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		updateScoreboard();
	}

	public boolean setColor(String colorName){
		try{
			partyColor = ChatColor.valueOf(colorName);
			for(String player:Members){
				try{
					Bukkit.getPlayer(player).setPlayerListName(partyColor+player);
				}catch(Exception f){
					System.out.println("[PartiesManager] Couldn't set player's name");
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
	
	
	private ArrayList<Player> onlinePlayers(){
		ArrayList<Player> result = new ArrayList<Player>();
		for(String member:Members){
			try{
				result.add(Bukkit.getPlayer(member));
			}
			catch(Exception e){
				System.out.println(member + " is offline");
			}
		}
		return result;
	}
	
	public void updateScoreboard(){
		objective.unregister();
		objective = scoreboard.registerNewObjective("test", "test");
		objective.setDisplayName(partyColor + name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		for(String player:Members){
			if(!isOwner(player)){
				try{
					objective.getScore(player + "  Lv.").setScore(Bukkit.getPlayer(player).getLevel());
					//Bukkit.getPlayer(player).setScoreboard(scoreboard);
				}
				catch (Exception e){}
			}
			else{
				try{
					objective.getScore("\u2605"+owner+"\u2605  Lv.").setScore(Bukkit.getPlayer(owner).getLevel());
					//Bukkit.getPlayer(player).setScoreboard(scoreboard);
				}
				catch(Exception e){}
			}
		}

	}
	
	//TODO: CREATE A SEPARATE ONLINE LIST
	//should be called less often than update
	public void createTrackers(){
		//System.out.println("creating trackers");
		for(Player owner:onlinePlayers()){
			for(Player target:onlinePlayers()){
				try{
					if(!owner.equals(target)){
						if(!hasTracker(owner, target)){
							TrackerStand newTracker = new TrackerStand(owner, target);
							trackers.add(newTracker);
							//System.out.println("Created new tracker");
						}
					}
				}
				catch (Exception e){}
			}
		}
	}
	
	private boolean hasTracker(Player owner, Player target){
		for(TrackerStand tracker:trackers){
			if(tracker.owner == owner && tracker.target == target){
				return true;
			}
		}
		return false;
	}
	
	public void updateTrackers(){
		Iterator<TrackerStand> iterator = trackers.iterator();
		while(iterator.hasNext()){
			TrackerStand tracker = iterator.next();
			if(!tracker.updatePosition(partyColor)){
				//System.out.println("deleting tracker");
				tracker.delete();
				iterator.remove();
			}
			else if(!inParty(tracker.target.getName()) || !inParty(tracker.owner.getName())){
				System.out.println("deleting tracker");
				tracker.delete();
				iterator.remove();
			}
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
				Bukkit.getPlayer(player).setScoreboard(scoreboard);
			}
			catch(Exception e){
	
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
						try{
							Bukkit.getPlayer(playerName).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
							team.removePlayer(Bukkit.getOfflinePlayer(playerName));
							Bukkit.getPlayer(playerName).setDisplayName(playerName);
						}catch(Exception e){}
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
