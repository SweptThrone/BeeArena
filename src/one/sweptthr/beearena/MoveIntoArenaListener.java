package one.sweptthr.beearena;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.sk89q.worldedit.math.BlockVector3;

public class MoveIntoArenaListener implements Listener {
	private final BeeArena plugin;
	
	public MoveIntoArenaListener( BeeArena plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMove( PlayerMoveEvent event ) {
		if ( this.plugin.hiveRegion == null ) { return; }
		
		Location loc = event.getTo();
		if ( ( !event.getPlayer().hasMetadata( "IsInHive" ) || event.getPlayer().getMetadata( "IsInHive" ).get( 0 ).asBoolean() == false ) && this.plugin.hiveRegion.contains( BlockVector3.at( loc.getX(), loc.getY(), loc.getZ() ) ) ) {
			event.getPlayer().setMetadata( "IsInHive", new FixedMetadataValue( this.plugin, true ) );
			this.plugin.hivePlayers++;
			for ( Bee bee : this.plugin.getBeesInHive() ) {
				if ( bee.getTarget() == null || bee.getTarget().isDead() && this.plugin.hivePlayers > 0 ) {
					bee.setTarget( event.getPlayer() );
					bee.setAnger( Integer.MAX_VALUE );
				}
			}
			//this.plugin.getLogger().info( "Player entered the Hive" );
		} else if ( ( event.getPlayer().hasMetadata( "IsInHive" ) && event.getPlayer().getMetadata( "IsInHive" ).get( 0 ).asBoolean() == true ) && !this.plugin.hiveRegion.contains( BlockVector3.at( loc.getX(), loc.getY(), loc.getZ() ) ) ) {
			event.getPlayer().setMetadata( "IsInHive", new FixedMetadataValue( this.plugin, false ) );
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
			//this.plugin.getLogger().info( "Player left the Hive (stepped out)" );
		}
	}
}
