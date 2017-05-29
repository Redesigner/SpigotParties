package parties;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyMessage implements CommandExecutor{

	private PartyList partyList;
	
	public PartyMessage(PartyList partyList){
		this.partyList = partyList;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String message = "";
			for(String text:args){
				message += text;
			}
			partyList.getUserParty(player.getName()).message(message, player);
		}
		return false;
	}

}
