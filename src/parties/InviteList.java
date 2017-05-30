package parties;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InviteList {

	public static ArrayList<Invite> Invites;
	private PartyList partyList;
	
	public InviteList(PartyList partyList){
		Invites = new ArrayList<Invite>();
		this.partyList = partyList;
	}
	
	public static void register(Invite invite){
		if(!Invites.contains(invite)){
			Invites.add(invite);
		}
	}
	
	public void decline(Player player){
		Invite deletion = null;
		boolean canDelete = false;
		//I don't feel like using iterators
		for(Invite invite:Invites){
			if(invite.target == player){
				player.sendMessage("You declined the party invite");
				canDelete = true;
				deletion = invite;
				try{
					invite.getSender().sendMessage(player.getName() + " has declined your invite");
				}
				catch(Exception e){}// we already did this!
			}
		}
		if(canDelete){
			Invites.remove(deletion);
		}
	}
	
	public void accept(Player player){
		for(Invite invite:Invites){
			if(invite.target == player){
				player.sendMessage("Welcome to " + invite.getParty().name + "!");
				invite.getParty().sendMessage(player.getName() + " has joined the party!");
				invite.getParty().addMember(player.getName());
			}
		}
	}
	
	public boolean hasInvite(Player player){
		for(Invite invite:Invites){
			if(invite.target == player){
				return true;
			}
		}
		return false;
	}
	
	public void update(){
		Iterator<Invite> iterator = Invites.iterator();
		while(iterator.hasNext()){
			Invite invite = iterator.next();
			if(partyList.inAnyParty(invite.target.getName())){
				invite.delete();
				iterator.remove();
			}
			if(invite.isExpired()){
				try{
					invite.target.sendMessage("Your invite to "+ invite.getParty().getColor() + invite.getParty().name + ChatColor.WHITE + " has expired");
					invite.getSender().sendMessage("Your invite has expired");
				}
				catch(Exception e){
					System.out.println("[PartiesManager] Couldn't send message! Is the recipient offline?");
				}
				invite.delete();
				iterator.remove();
			}
		}
	}
}
