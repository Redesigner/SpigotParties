package parties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PartyCommand implements CommandExecutor {
	
	private PartyList partyList;
	private InviteList inviteList;
	private JavaPlugin root;
	
	public PartyCommand(PartyList list, InviteList invites, JavaPlugin root){
		partyList = list;
		inviteList = invites;
		this.root = root;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command party, String commandlabel, String[] arg3) {
		if (sender instanceof Player) {
			if(arg3.length == 1){
				Player player = (Player) sender;
				String id = player.getName();
				String subcommand = arg3[0];
				if(subcommand.equals("leave")){
					if(!partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "notinparty");
					}
					else{
						partyList.getUserParty(id).removeMember(id);
						MessageLoader.sendMessageArray(player, "leavesuccess");
					}
					return true;
				}
				
				if(subcommand.equals("list")){
					player.sendMessage("There are "+partyList.getParties().size() + " parties");
					for(Party list:partyList.getParties()){
						player.sendMessage(list.name);
					}
					return true;
				}
				
				if(subcommand.equals("members")){
					if(partyList.inAnyParty(id)){
						player.sendMessage(partyList.getUserParty(id).name + " has " + partyList.getUserParty(id).Members.size() + " members:");
						player.sendMessage("\u2605" + partyList.getUserParty(id).getOwner() + "\u2605");
						for(String output:partyList.getUserParty(id).Members){
							if(!partyList.getUserParty(id).isOwner(output)){
								player.sendMessage(output);
							}
						}
					}
					return true;
				}
				
				if(subcommand.equals("accept")){
					inviteList.accept(player);
					return true;
				}
				
				if(subcommand.equals("decline")){
					inviteList.decline(player);
					return true;
				}
				
				if(subcommand.equals("help")){
					MessageLoader.sendMessageArray(player, "helpmessage");
					return true;
				}
			}
			
			if (arg3.length == 2){
				Player player = (Player) sender;
				String id = player.getName();
				String subcommand = arg3[0];
				String name = arg3[1];
				
				if(subcommand.equals("create")) {
					if(partyList.inAnyParty(id)){
						MessageLoader.sendMessageArray(player, "alreadyinparty");
					}
					else if(partyList.getParty(name)==null){
						Party newParty = new Party(name, partyList, root);
						partyList.register(newParty);
						newParty.addMember(id);
						newParty.setOwner(id);
						MessageLoader.sendMessageArray(player, "partycreatesuccess");
					}
					else{
						MessageLoader.sendMessageArray(player, "partyexists");
					}
					return true;
				}
				
				if(subcommand.equals("rename")){
					if(!partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "notinparty");	
					}
					else if(partyList.partyExists(name)){
						MessageLoader.sendMessageArray(player, "partyexists");
					}
					else if(partyList.getUserParty(id).isOwner(id)) {
						partyList.getUserParty(id).rename(name);
						player.sendMessage("Renamed party to '" + name + "'");
					}
					else{
						player.sendMessage("You aren't the owner!");
					}
					return true;

				}
				
				if(subcommand.equals("join")){
					if(partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "invalidjoin");
					}
					else if(partyList.partyExists(name)){
						partyList.getParty(name).addMember(id);
						player.sendMessage("Successfully joined '" + name + "'");
					}
					return true;
				}
				
				if(subcommand.equals("setcolor")){
					name = name.toUpperCase();
					if(!partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "notinparty");
					}
					else if(partyList.inAnyParty(id)){
						if(partyList.getUserParty(id).isOwner(id)){
							if(partyList.getUserParty(id).setColor(name)){
								player.sendMessage("Changed party color to: " + name);
							}
							else{
								player.sendMessage("That is not a valid color, try /party help colors");
							}

						}
						else{
							player.sendMessage("You must be the owner to change the color!");
						}
					}
					return true;
				}
				
				if(subcommand.equals("setowner")){
					if(!partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "notinparty");
					}
					else if(partyList.inAnyParty(id)){
						if(partyList.getUserParty(id).isOwner(id) && partyList.getUserParty(id).inParty(name)){
							partyList.getUserParty(id).setOwner(name);
							player.sendMessage("Set owner as: " + name);
						}
					}
					return true;
				}
				
				if(subcommand.equals("help") && name.equals("color")){
					player.sendMessage("Valid party colors:");
					player.sendMessage(ChatColor.BLACK + "black " +
					ChatColor.DARK_BLUE + "dark_blue " +
					ChatColor.DARK_AQUA + "dark_aqua " +
					ChatColor.DARK_RED + "dark_red " + 
					ChatColor.DARK_PURPLE + "dark_purple " + 
					ChatColor.GOLD + "gold " + 
					ChatColor.GRAY + "gray "+ 
					ChatColor.DARK_GRAY + "dark_gray " + 
					ChatColor.BLUE + "blue " + 
					ChatColor.GREEN + "green " + 
					ChatColor.AQUA + "aqua " + 
					ChatColor.RED + "red " + 
					ChatColor.LIGHT_PURPLE + "light_purple " + 
					ChatColor.YELLOW + "yellow " + 
					ChatColor.WHITE + "white");
				}
				
				if(subcommand.equals("kick")){
					if(!partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "notinparty");
					}
					else if(partyList.inAnyParty(id)){
						if(partyList.getUserParty(id).isOwner(id) && partyList.getUserParty(id).inParty(name)){
							partyList.getUserParty(id).sendMessage(name + " has been kicked out of the party");
							partyList.getUserParty(id).removeMember(name);
							try{
								Bukkit.getPlayer(name).sendMessage("You have been kicked from the party");
							}
							catch(Exception e){
								System.out.println("[PartyManager] couldn't deliver message! is the user online?");
							}
						}
					}
					return true;
				}
				
				if(subcommand.equals("invite")){
					if(!partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "notinparty");
					}
					else{
						try{
							Player target = Bukkit.getPlayer(name);
							if(partyList.inAnyParty(name)){
								player.sendMessage("That player is already in a party");
								return true;
							}
							if(inviteList.hasInvite(target)){
								player.sendMessage("That player already has a pending invite!");
								return true;
							}
							Invite invite = new Invite(target, partyList.getUserParty(id), id);
							InviteList.register(invite);
							invite.display();
						}
						catch(Exception e){
							player.sendMessage("That player doesn't exist");
						}
					}
				}
				return true;
			}
			Player player = (Player) sender;
			player.sendMessage("Invalid command!");
			player.sendMessage("Try using '/party help' for more information");
		}

		return false;
	}

}
