package one.sweptthr.beearena;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BeeRearmer extends BukkitRunnable {
	private Bee bee;
	private Player ply;
	private final BeeArena plugin;
	
	public BeeRearmer( BeeArena plugin, Bee bee, Player ply ) {
		this.plugin = plugin;
		this.bee = bee;
		this.ply = ply;
	}

	@Override
	public void run() {
		if ( this.ply.hasMetadata( "IsInHive" ) && this.ply.getMetadata( "IsInHive" ).get( 0 ).asBoolean() ) {
			this.bee.setTarget( this.ply );
			this.bee.setAnger( Integer.MAX_VALUE );
		} else if ( this.plugin.hivePlayers > 0 ) {
			Random rand = new Random();

			ArrayList< Player > plys = this.plugin.getPlayersInHive();
			this.bee.setTarget( plys.get( plys.size() == 1 ? 0 : rand.nextInt( 0, plys.size() - 1 ) ) );
			this.bee.setAnger( Integer.MAX_VALUE );
		}
		this.bee.setHasStung( false );
	}

}
