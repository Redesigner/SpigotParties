package parties;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginEvent implements Listener {
	
	private PartyList partyList;
	
	public LoginEvent(PartyList partyList){
		this.partyList = partyList;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	if(partyList.inAnyParty(event.getPlayer().getName())){
    		partyList.getUserParty(event.getPlayer().getName()).login(event.getPlayer());
    	}
    	else{
    		event.getPlayer().setDisplayName(event.getPlayer().getName());
    	}
    }
	
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
    	System.out.println("Player logged out");
    	if(partyList.inAnyParty(event.getPlayer().getName())){
    		partyList.getUserParty(event.getPlayer().getName()).logout(event.getPlayer());
    	}
    }
}