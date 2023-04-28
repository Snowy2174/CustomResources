package plugin.customresources.listeners;

import plugin.customresources.controllers.PlayerExtractionLimitsController;
import plugin.customresources.settings.CustomResourcesSettings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * 
 * @author Goosius
 *
 */
public class CustomResourcesBukkitEventListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if(CustomResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processEntityDamageByEntityEvent(event);
		}
	}

	@EventHandler()
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if(CustomResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processEntityDeathEvent(event);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if(CustomResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processBlockBreakEvent(event);
		}	
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockShearEntityEvent(BlockShearEntityEvent event) {
		if(CustomResourcesSettings.isEnabled() && CustomResourcesSettings.areResourceExtractionLimitsEnabled() && CustomResourcesSettings.areShearingExtractionLimitsEnabled()) {
			//Dispensers cannot shear entities
			event.setCancelled(true);
		}	
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerShearEntityEvent(PlayerShearEntityEvent event) {
		if(CustomResourcesSettings.isEnabled() && CustomResourcesSettings.areResourceExtractionLimitsEnabled() && CustomResourcesSettings.areShearingExtractionLimitsEnabled()) {
			PlayerExtractionLimitsController.processPlayerShearEntityEvent(event);
		}	
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void onItemSpawnEvent(ItemSpawnEvent event) {
		if(CustomResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processItemSpawnEvent(event);
		}	
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerFishEvent(PlayerFishEvent event) {
		if(CustomResourcesSettings.isEnabled()) {
			PlayerExtractionLimitsController.processPlayerFishEvent(event);
		}	
	}
	
	
	@EventHandler()
	public void onPlayerLoginEvent(PlayerLoginEvent event) {
		if(CustomResourcesSettings.isEnabled()) {		
			PlayerExtractionLimitsController.processPlayerLoginEvent(event);
		}			
	}

	@EventHandler()
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		if(CustomResourcesSettings.isEnabled()) {		
			PlayerExtractionLimitsController.processPlayerQuitEvent(event);
		}			
	}
}
