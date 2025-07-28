// package
package one.sweptthr.beearena;
// java imports
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// base imports
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;

// WorldGuard imports
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldedit.bukkit.BukkitAdapter;

// misc import
import com.google.common.collect.ImmutableList;

/* TODO
 * remove debug prints
 * TEST active bee spawns X seconds after player walks in (config)
 * TEST limit bee spawns
 * handle despawning according to requests
 */

public class BeeArena extends JavaPlugin {
	public YamlConfiguration config;
	public File configFile;
	public ProtectedRegion hiveRegion;
	public int hivePlayers = 0;
	//public int hiveBees = 0;
	public BeeSpawner beeSpawner;
	
	/** Saves the current config data structure to disk at /plugins/BeeArena/config.yml
	 @throws IOException On file write failure */
	public void saveConfig() {
		try {
			this.config.save( this.configFile );
		} catch ( IOException err ) {
			this.getLogger().severe( "A fatal error has occurred while saving the config file.  This might cause issues!" );
		}
	}
	
	/** Loads the file at /plugins/BeeArena/config.yml into the config data structure
	 * 
	 * @return The <code>YamlConfiguration</code> parsed from the config file
	 */
	public YamlConfiguration loadConfig() {
		return YamlConfiguration.loadConfiguration( this.configFile );
	}
	
	/** Applies the config file to the plugin's variables.<br>
	 * This sets the region the plugin uses as the Hive.
	 * 
	 * @return <code>true</code> on success, <code>false</code> on failure
	 */
	public boolean applyConfig() {
		final String worldName = this.config.getString( "arenaWorld" );
		final String regionName = this.config.getString( "arenaRegionName" );
		
		org.bukkit.World world = Bukkit.getServer().getWorld( worldName );
		if ( world == null ) {
			this.getLogger().severe( "Could not find the world \"" + worldName + "\" specified.  Use command beearena:setregion <world> <region> to specify a region in a world." );
			this.hiveRegion = null;
			return false;
		}
		com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt( world );
		RegionContainer allRegions = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager worldRegions = allRegions.get( weWorld );
		this.hiveRegion = worldRegions.getRegion( regionName );
		if ( this.hiveRegion == null ) {
			this.getLogger().severe( "Could not find the region \"" + regionName + "\" specified in world \"" + worldName + "\".  Use command beearena:setregion <world> <region> to specify a region in a world." );
			return false;
		}
		
		return true;
	}
	
	public ArrayList< Player > getPlayersInHive() {
		ArrayList< Player > retVal = new ArrayList< Player >();
		
		for ( Player ply : ImmutableList.copyOf( this.getServer().getOnlinePlayers() ) ) {
			if ( !ply.isDead() && ply.hasMetadata( "IsInHive" ) && ply.getMetadata( "IsInHive" ).get( 0 ).asBoolean() ) {
				retVal.add( ply );
			}
		}
		
		return retVal;
	}
	
	public ArrayList< Bee > getBeesInHive() {
		ArrayList<Bee> retVal = new ArrayList< Bee >();
		
		org.bukkit.World world = Bukkit.getServer().getWorld( this.config.getString( "arenaWorld" ) );
		
		for ( Bee bee : world.getEntitiesByClass( Bee.class ) ) {
			if ( bee.hasMetadata( "IsHiveBee" ) && bee.getMetadata( "IsHiveBee" ).get( 0 ).asBoolean() && !bee.isDead() && bee.isValid() ) {
				retVal.add( bee );
			}
		}
		
		return retVal;
	}
	
	@Override
	public void onEnable() {
		// [the server prints a message by default]
		
		// add event handler
		this.getServer().getPluginManager().registerEvents( new MoveIntoArenaListener( this ), this );
		this.getServer().getPluginManager().registerEvents( new LeaveServerListener( this ), this );
		this.getServer().getPluginManager().registerEvents( new DieInArenaListener( this ), this );
		this.getServer().getPluginManager().registerEvents( new JoinServerListener( this ), this );
		this.getServer().getPluginManager().registerEvents( new BeeDamageListener( this ), this );
		this.getServer().getPluginManager().registerEvents( new ArenaBeeDeathListener( this ), this );
		
		// create this plugin's data folder and default config
		if ( this.getDataFolder().mkdir() ) {
			this.getLogger().info( "Created data folder" );
		}
		this.configFile = new File( this.getDataFolder(), "config.yml" );
		this.config = new YamlConfiguration();
		if ( this.configFile.exists() ) {
			this.config = this.loadConfig();
			this.getLogger().info( "Loaded existing config." );
		} else {
			List< String > vWeps = new ArrayList< String >();
			vWeps.add( "§aFlimsy Sword" );
			
			this.config.set( "arenaRegionName", "beehive" );
			this.config.set( "arenaWorld", "world" );
			this.config.set( "maxArenaBees", 10 );
			this.config.set( "beeRespawnRateMS", 30000 );
			this.config.set( "beeHarmingWeapons", vWeps );
			this.saveConfig();
			this.getLogger().info( "Created new config." );
		}
		
		this.applyConfig();
		
		this.beeSpawner = new BeeSpawner( this );
		this.beeSpawner.runTaskTimer( this, 0L, this.config.getLong( "beeRespawnRateMS" ) / 1000L * 20L ); // from ms to s to t
	}
	
	@Override
	public void onDisable() {
		// [the server prints a message by default]
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( command.getName().equalsIgnoreCase( "beearena" ) ) {
			sender.sendMessage( "§eBeeArena §fby §aSweptThrone" );
			return true;
		
		} else if ( command.getName().equalsIgnoreCase( "spawninterval" ) ) {
			if ( sender.hasPermission( "beearena.admin" ) || sender.isOp() ) {
				if ( args.length < 1 ) {
					return false;
				}
				
				int spawnInterval;
				
				try {
					spawnInterval = Integer.valueOf( args[ 0 ] );
					if ( spawnInterval <= 0 ) {
						sender.sendMessage( "§cThe value you supply must be a positive number." );
						return true;
					}
					this.config.set( "beeRespawnRateMS", spawnInterval );
					this.saveConfig();
					this.beeSpawner.cancel();
					this.beeSpawner = new BeeSpawner( this );
					this.beeSpawner.runTaskTimer( this, 0L, this.config.getLong( "beeRespawnRateMS" ) / 1000L * 20L ); // from ms to s to t
					sender.sendMessage( "§aHive bee respawn interval has been updated to " + spawnInterval + "ms and saved!" );
					return true;
				} catch ( NumberFormatException err ) {
					sender.sendMessage( "§cThe value you supply must be a whole number." );
					return true;
				}
			} else {
				sender.sendMessage( "§cYou do not have permission to use this command." );
				return true;
			}
			
		} else if ( command.getName().equalsIgnoreCase( "maxbees" ) ) {
			if ( sender.hasPermission( "beearena.admin" ) || sender.isOp() ) {
				if ( args.length < 1 ) {
					return false;
				}
				
				int maxBees;
				
				try {
					maxBees = Integer.valueOf( args[ 0 ] );
					if ( maxBees <= 0 ) {
						sender.sendMessage( "§cThe value you supply must be a positive number." );
						return true;
					}
					this.config.set( "maxArenaBees", maxBees );
					this.saveConfig();
					sender.sendMessage( "§aMax bees allowed in the hive has been updated to " + maxBees + " bees and saved!" );
					return true;
				} catch ( NumberFormatException err ) {
					sender.sendMessage( "§cThe value you supply must be a whole number." );
					return true;
				}
			} else {
				sender.sendMessage( "§cYou do not have permission to use this command." );
				return true;
			}
			
		} else if ( command.getName().equalsIgnoreCase( "addweapon" ) ) {
			if ( sender.hasPermission( "beearena.admin" ) || sender.isOp() ) {
				if ( !( sender instanceof Player ) ) {
					sender.sendMessage( "§cOnly players can use this command!" );
					return true;
				}
				Player ply = ( Player )sender;
				List< String > validWeps = this.config.getStringList( "beeHarmingWeapons" );
				
				if ( ply.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() ) {
					if ( validWeps.indexOf( ply.getInventory().getItemInMainHand().getItemMeta().getDisplayName() ) != -1 ) {
						sender.sendMessage( "§cThe name of the item in your hand is already in the list of bee-harming weapons!" );
						return true;
					}
					validWeps.add( ply.getInventory().getItemInMainHand().getItemMeta().getDisplayName() );
					this.config.set( "beeHarmingWeapons", validWeps );
					this.saveConfig();
					sender.sendMessage( "§aThe name of the item in your hand has been added to the list of bee-harming weapons!  You can edit the config file to remove it." );
					return true;
				} else {
					sender.sendMessage( "§cThe name of the item in your hand has no custom name!  Bee-harming weapons must have custom names." );
					return true;
				}
			} else {
				sender.sendMessage( "§cYou do not have permission to use this command." );
				return true;
			}
			
		} else if ( command.getName().equalsIgnoreCase( "setregion" ) ) {
			if ( sender.hasPermission( "beearena.admin" ) || sender.isOp() ) {
				if ( args.length < 2 ) {
					return false;
				}
				final String world = args[ 0 ], region = args[ 1 ];
				this.config.set( "arenaWorld", world );
				this.config.set( "arenaRegionName", region );
				this.saveConfig();
				final boolean succ = this.applyConfig();
				if ( succ ) {
					sender.sendMessage( "§aBeeArena config has been updated and saved!" );
				} else {
					sender.sendMessage( "§cBeeArena config has been updated and saved, but either the world or region could not be found.\nIf you create the world or region after this, you will have to run the command again." );
				}
				return true;
			} else {
				sender.sendMessage( "§cYou do not have permission to use this command." );
				return true;
			}
		}
		return false;
	}
}
