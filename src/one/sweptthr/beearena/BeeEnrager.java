package one.sweptthr.beearena;

import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BeeEnrager extends BukkitRunnable {
	private final BeeArena plugin;
	private final Bee bee;
	private final Player ply;
	private final BeeRearmer rearmer;
	
	public BeeEnrager( BeeArena plugin, Bee bee, Player ply ) {
		this.plugin = plugin;
		this.bee = bee;
		this.ply = ply;
		this.rearmer = new BeeRearmer( this.plugin, this.bee, this.ply );
	}
	
	@Override
	public void run() {
		this.bee.setAnger( Integer.MAX_VALUE );
		this.bee.setTarget( this.ply );
		this.rearmer.runTaskLater( this.plugin, 2L * 20L );
	}

}
