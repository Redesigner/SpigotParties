package parties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TrackerStand {

	private ArmorStand armorStand;
	public Player owner;
	public Player target;
	private ChatColor color;
	
	public TrackerStand(Player owner, Player target){
		this.owner = owner;
		this.target = target;
		armorStand = Bukkit.getWorld("world").spawn(owner.getLocation(), ArmorStand.class);
		armorStand.setGravity(false);
		armorStand.setInvulnerable(true);
		armorStand.setCustomNameVisible(true);
		armorStand.setVisible(false);
		armorStand.setMarker(true);
		color = ChatColor.WHITE;
		//armorStand.playEffect(EntityEffect.)
	}
	
	public boolean updatePosition(ChatColor color){
		this.color = color;
		try{
			if(owner.getLocation().distance(target.getLocation())<= 16){
				armorStand.setCustomNameVisible(false);
			}
			else{
				Location distance = owner.getLocation().subtract(target.getLocation());
				Vector offset = distance.toVector().normalize().multiply(-5);
				armorStand.teleport(offset.toLocation(Bukkit.getWorld("world")).add(owner.getLocation()).add(0,.8,0));
				armorStand.setCustomName(this.color + target.getName() + " " + (int)Math.floor(owner.getLocation().distance(target.getLocation())) + "m");
				armorStand.setCustomNameVisible(true);
			}

			return true;
		}
		catch(Exception e){
			//System.out.println("[PartyManager]" + e);
			return false;
		}
	}
	
	public void delete(){
		armorStand.remove();
	}
}
