package parties;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PartyCommand implements CommandExecutor {
	
	private PartyList partyList;
	
	public PartyCommand(PartyList list){
		partyList = list;
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
						Party newParty = new Party(name, partyList);
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
					if(!partyList.inAnyParty(id)) {
						MessageLoader.sendMessageArray(player, "notinparty");
					}
					else if(partyList.inAnyParty(id)){
						if(partyList.getUserParty(id).isOwner(id)){
							partyList.getUserParty(id).setColor(name);
							player.sendMessage("Changed party color to: " + name);
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
			}
			Player player = (Player) sender;
			player.sendMessage("Invalid command!");
			player.sendMessage("Try using '/party help' for more information");
		}

		return false;
	}

}
