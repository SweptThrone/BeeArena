package one.sweptthr.beearena;

import java.util.List;

import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BeeDamageListener implements Listener {
	private final BeeArena plugin;
	
	public BeeDamageListener( BeeArena plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBeeHurt( EntityDamageByEntityEvent event ) {
		if ( !event.getEntity().hasMetadata( "IsHiveBee" ) || event.getEntity().getMetadata( "IsHiveBee" ).get( 0 ).asBoolean() == false ) { return; }
		if ( event.getDamager().getType() != EntityType.PLAYER ) { return; }
		
		Player ply = ( Player )event.getDamager();
		
		event.setCancelled( true );
		
		List< String > vWeps = this.plugin.config.getStringList( "beeHarmingWeapons" );
		
		if ( ply.getInventory().getItemInMainHand().getItemMeta() != null 
			&& ply.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() 
			&& ( vWeps.indexOf( ply.getInventory().getItemInMainHand().getItemMeta().getDisplayName() ) != -1 ) ) {
			
			//this.plugin.getLogger().info( "uncancelled" );
			event.setCancelled( false );
		}
		
	}
	
	@EventHandler
	public void onBeeSting( EntityDamageByEntityEvent event ) {
		if ( event.getEntity().getType() != EntityType.PLAYER ) { return; }
		if ( !event.getDamager().hasMetadata( "IsHiveBee" ) || event.getDamager().getMetadata( "IsHiveBee" ).get( 0 ).asBoolean() == false ) { return; }
		
		//this.plugin.getLogger().info( "get stung nerd" );
		
		Player ply = ( Player )event.getEntity();
		Bee bee = ( Bee )event.getDamager();
		
		BeeEnrager beeEnrager = new BeeEnrager( this.plugin, bee, ply );
		beeEnrager.runTaskLater( this.plugin, 1L );
	}
}
