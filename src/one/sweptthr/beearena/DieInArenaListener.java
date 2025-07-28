package one.sweptthr.beearena;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class DieInArenaListener implements Listener {
	private final BeeArena plugin;
	
	public DieInArenaListener( BeeArena plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDieInArena( PlayerDeathEvent event ) {
		if ( this.plugin.hiveRegion == null ) { return; }
		
		if ( event.getEntity().hasMetadata( "IsInHive" ) && event.getEntity().getMetadata( "IsInHive" ).get( 0 ).asBoolean() == true ) {
			event.getEntity().setMetadata( "IsInHive", new FixedMetadataValue( this.plugin, false ) );
			this.plugin.hivePlayers--;
			if ( this.plugin.hivePlayers == 0 ) {
				//this.plugin.getLogger().info( "The hive is now empty" );
				for ( Bee bee : this.plugin.getBeesInHive() ) {
					bee.setTarget( null );
					bee.setAnger( 0 );
				}
			} else {
				for ( Bee bee : this.plugin.getBeesInHive() ) {
					if ( bee.getTarget() == null || bee.getTarget().isDead() || ( bee.getTarget().hasMetadata( "IsInHive" ) && !bee.getTarget().getMetadata( "IsInHive" ).get( 0 ).asBoolean() ) && this.plugin.hivePlayers > 0 ) {
						Random rand = new Random();

						ArrayList< Player > plys = this.plugin.getPlayersInHive();
						bee.setTarget( plys.get( plys.size() == 1 ? 0 : rand.nextInt( 0, plys.size() - 1 ) ) );
						bee.setAnger( Integer.MAX_VALUE );
					}
				}
			}
			//this.plugin.getLogger().info( "Player left the hive (died)" );
		}
	}

}
