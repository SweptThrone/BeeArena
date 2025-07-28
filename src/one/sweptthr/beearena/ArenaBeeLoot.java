package one.sweptthr.beearena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;

public class ArenaBeeLoot implements LootTable {
	private final BeeArena plugin;
	final NamespacedKey key;
	
	public ArenaBeeLoot( BeeArena plugin ) {
		this.plugin = plugin;
		key = new NamespacedKey( ( Plugin )this.plugin, "beearena" );
	}
	
	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	// we do not need this function
	public void fillInventory( Inventory inv, Random rand, LootContext ctx ) {}

	@Override
	public Collection< ItemStack > populateLoot( Random rand, LootContext ctx ) {
		final List< ItemStack > lootItems = new ArrayList< ItemStack >();
		
		if ( rand.nextDouble() <= 0.05 ) {
			final List< String > psLore = new ArrayList< String >();
			psLore.add( "§7A stinger from a" );
			psLore.add( "§7\"§4Man Huntin' Bee§7\"." );
			psLore.add( "§7Sharp as a knife," );
			psLore.add( "§7hard as steel." );
			
			ItemStack pStinger = new ItemStack( Material.FLINT, 1 );
			ItemMeta meta = pStinger.getItemMeta();
			
			meta.setDisplayName( "§e§oPristine§r §eStinger" );
			meta.setLore( psLore );
			pStinger.setItemMeta( meta );
			
			lootItems.add( pStinger );
		} else if ( rand.nextDouble() <= 0.45 ) {
			final List< String > sLore = new ArrayList< String >();
			sLore.add( "§7A stinger from a" );
			sLore.add( "§7\"§4Man Huntin' Bee§7\"." );
			sLore.add( "§7Big one too." );
			
			ItemStack stinger = new ItemStack( Material.FLINT, 1 );
			ItemMeta meta = stinger.getItemMeta();
			
			meta.setDisplayName( "§eBee Stinger" );
			meta.setLore( sLore );
			stinger.setItemMeta( meta );
			
			lootItems.add( stinger );
		}
		
		return lootItems;
	}
	
	

}
