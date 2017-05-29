package parties;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageLoader {
	
	private static JavaPlugin root;
	
	public MessageLoader(JavaPlugin owner){
		root = owner;
	}
	
	public static void sendMessageArray(Player recipient, String messageSource){
		String message = root.getConfig().getString("messages."+messageSource);
		try{
			String[] messageList = message.split("[$]");
			for(String messageSplit:messageList){
				recipient.sendMessage(messageSplit);
			}
		}
		catch(Exception e){
			root.getConfig().set("messages." + messageSource, "");
			root.saveConfig();
		}
	}
}
