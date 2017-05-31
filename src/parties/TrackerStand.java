package parties;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

public class TrackerStand {

	private ArmorStand armorStand;
	public Player owner;
	public Player target;
	private ChatColor color;
	private JavaPlugin root;
	
	public TrackerStand(Player owner, Player target, JavaPlugin root){
		this.owner = owner;
		this.target = target;
		this.root = root;
		armorStand = owner.getWorld().spawn(owner.getLocation(), ArmorStand.class);
		armorStand.setGravity(false);
		armorStand.setInvulnerable(true);
		armorStand.setVisible(false);
		armorStand.setMarker(true);
		armorStand.setCustomNameVisible(false);
		armorStand.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000, 1));
		color = ChatColor.WHITE;
		//setVisible(owner);
		//armorStand.playEffect(EntityEffect.)
	}
	
	public boolean updatePosition(ChatColor color){
		this.color = color;
		if(owner.isOnline() && target.isOnline()){
			if(owner.getWorld()==target.getWorld()){
				if(owner.getLocation().distance(target.getLocation())<= 16){
					setVisible(owner, false);
					return true;
				}
				else{
					Location distance = owner.getLocation().subtract(target.getLocation());
					Vector offset = distance.toVector().normalize().multiply(-5);
					armorStand.teleport(offset.toLocation(owner.getWorld()).add(owner.getLocation()).add(0,1.2,0));
					armorStand.setCustomName(this.color + "<" + target.getName() + "> " + (int)Math.floor(owner.getLocation().distance(target.getLocation())) + "m");
					setVisible(owner, true);
					return true;
				}
			}
			else{
				setVisible(owner, false);
				return true;
			}

		}
		return false;

	}
	
	public void setVisible(Player player, boolean value){
		//System.out.println(armorStand.getCustomName());
		PacketContainer packet = ((partychat) root).protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet.getIntegers().write(0, armorStand.getEntityId());
	    WrappedDataWatcher watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
	    Serializer serializer = Registry.get(Boolean.class); //Found this through google, needed for some stupid reason
	    watcher.setEntity(armorStand); //Set the new data watcher's target
	    WrappedDataWatcherObject object = new WrappedDataWatcherObject(3, serializer);
	    watcher.setObject(object, value);
	    packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects()); //Make the packet's datawatcher the one we created
	    try {
	        ((partychat) root).protocolManager.sendServerPacket(player, packet);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
				
	}

	
	public void delete(){
		System.out.println("Armor stand tracker deleted");
		armorStand.remove();
	}
}
