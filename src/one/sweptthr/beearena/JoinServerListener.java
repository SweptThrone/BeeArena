package one.sweptthr.beearena;

import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.sk89q.worldedit.math.BlockVector3;

public class JoinServerListener implements Listener {
	private final BeeArena plugin;
	
	public JoinServerListener( BeeArena plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoinServer( PlayerJoinEvent event ) {
		if ( this.plugin.hiveRegion == null ) { return; }
		
		Location loc = event.getPlayer().getLocation();
		if ( ( !event.getPlayer().hasMetadata( "IsInHive" ) || event.getPlayer().getMetadata( "IsInHive" ).get( 0 ).asBoolean() == false ) && this.plugin.hiveRegion.contains( BlockVector3.at( loc.getX(), loc.getY(), loc.getZ() ) ) && !event.getPlayer().isDead() ) {
			event.getPlayer().setMetadata( "IsInHive", new FixedMetadataValue( this.plugin, true ) );
			this.plugin.hivePlayers++;
			for ( Bee bee : this.plugin.getBeesInHive() ) {
				if ( bee.getTarget() == null || bee.getTarget().isDead() && this.plugin.hivePlayers > 0 ) {
					bee.setTarget( event.getPlayer() );
					bee.setAnger( Integer.MAX_VALUE );
				}
			}
			//this.plugin.getLogger().info( "Player entered the Hive" );
		}
		
	}

}
