package parties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;

public class Invite {

	public Player target;
	private Party owner;
	private String sender;
	private Scoreboard scoreboard;
	final int MAX_INVITE_TIME = 30;
	private int expireTime;
	
	public Invite(Player player, Party party, String string){
		target = player;
		owner = party;
		sender = string;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		expireTime = MAX_INVITE_TIME;
		
	}
	
	public void display(){
		
		Objective objective = scoreboard.registerNewObjective("test", "test");
		objective.setDisplayName(ChatColor.BOLD + "Invite to " + owner.getColor() + owner.name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.getScore("from: " + sender).setScore(3);
		objective.getScore("/party accept").setScore(2);
		objective.getScore("/party decline").setScore(1);
		target.setScoreboard(scoreboard);
	}
	
	public Player getSender(){
		try{
			return Bukkit.getPlayer(sender);
		}
		catch(Exception e){
			System.out.println("[PartiesManager] Attempted to send message to a player who has logged out");
			return null;
		}
	}
	
	public Party getParty(){
		return owner;
	}
	
	public void update(){
		expireTime--;
	}
	
	public boolean isExpired(){
		return (expireTime<=0);
	}
	
	public void delete(){
		target.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
}
