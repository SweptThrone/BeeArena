package one.sweptthr.beearena;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
//import org.bukkit.loot.LootContext;
import org.bukkit.metadata.FixedMetadataValue;

public class BeeSpawner extends BukkitRunnable {
	private final BeeArena plugin;
	
	public BeeSpawner( BeeArena plugin ) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {		
		if ( this.plugin.hivePlayers <= 0 ) { return; }
		if ( this.plugin.hiveRegion == null ) { return; }
	
		ArrayList< Player > plys = this.plugin.getPlayersInHive();
		
		for ( int i = 0; i < this.plugin.hivePlayers; i++ ) {
			for ( int b = 0; b < 5; b++ ) {
				if ( this.plugin.getBeesInHive().size() >= this.plugin.config.getInt( "maxArenaBees" ) ) { break; }
				
				Random rand = new Random();
				
				org.bukkit.World world = this.plugin.getServer().getWorld( this.plugin.config.getString( "arenaWorld" ) );
				
				// this could potentially cause lag if a large number of checks fail.  too bad!
				Location randomLoc;
				do {
					BlockVector3 min = this.plugin.hiveRegion.getMinimumPoint(), max = this.plugin.hiveRegion.getMaximumPoint();
					double rx = Math.floor( rand.nextDouble( min.getX(), max.getX() ) ) + 0.5, ry = Math.floor( rand.nextDouble( min.getY(), max.getY() ) ) + 0.5, rz = Math.floor( rand.nextDouble( min.getZ(), max.getZ() ) ) + 0.5;
					
					randomLoc = new Location( world, rx, ry, rz );
				} while ( world.getBlockAt( randomLoc ).getType() != Material.AIR );
				
				//this.plugin.hiveBees++;
				Bee bee = ( Bee )world.spawnEntity( randomLoc, EntityType.BEE );
				bee.setMetadata( "IsHiveBee", new FixedMetadataValue( this.plugin, true ) );
				bee.setAnger( Integer.MAX_VALUE );
				bee.setTarget( plys.get( i ) );
				bee.setCustomName( "§4§lMan Huntin' Bee" );
				bee.setCustomNameVisible( true );
				
				BeeKiller killer = new BeeKiller( bee );
				killer.runTaskLater( this.plugin, ( this.plugin.config.getLong( "beeRespawnRateMS" ) / 1000L * 20L ) + 40L );
			}
		}
	}
}
