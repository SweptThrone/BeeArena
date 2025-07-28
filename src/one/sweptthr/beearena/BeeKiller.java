package one.sweptthr.beearena;

import org.bukkit.entity.Bee;
import org.bukkit.scheduler.BukkitRunnable;

public class BeeKiller extends BukkitRunnable {
	//private final BeeArena plugin;
	private final Bee bee;

	public BeeKiller( Bee bee ) {
		//this.plugin = plugin;
		this.bee = bee;
	}
	
	
	@Override
	public void run() {
		if ( !this.bee.isValid() || !this.bee.isDead() ) {
			this.bee.remove();
			//this.plugin.hiveBees--;
		}
	}

}
