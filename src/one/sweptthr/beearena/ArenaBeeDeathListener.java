package one.sweptthr.beearena;

import java.util.Collection;
import java.util.Random;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

public class ArenaBeeDeathListener implements Listener {
	private final BeeArena plugin;
	
	public ArenaBeeDeathListener( BeeArena plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onArenaBeeDeath( EntityDeathEvent event ) {
		if ( !event.getEntity().hasMetadata( "IsHiveBee" ) || !event.getEntity().getMetadata( "IsHiveBee" ).get( 0 ).asBoolean() ) { return; }
		//this.plugin.hiveBees--;
		if ( event.getEntity().getLastDamageCause() == null ) { return; }
		if ( !( event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent ) ) { return; }
		
		EntityDamageByEntityEvent dEvent = ( EntityDamageByEntityEvent ) event.getEntity().getLastDamageCause();
		
		if ( dEvent.getDamager().getType() != EntityType.PLAYER ) { return; }
		
		final Player ply = ( Player ) dEvent.getDamager();
		
		LootContext ctx = new LootContext.Builder( dEvent.getEntity().getLocation() )
							.killer( ply )
							.lootedEntity( dEvent.getEntity() )
							.lootingModifier( 0 )
							.build();
		final Collection< ItemStack > lootItems = new ArenaBeeLoot( this.plugin ).populateLoot( new Random(), ctx );
		
		event.getDrops().clear();
		event.getDrops().addAll( lootItems );
	}

}
