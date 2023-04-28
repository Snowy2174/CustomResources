package plugin.customresources.listeners;

import com.palmergames.bukkit.towny.event.PreNewDayEvent;
import com.palmergames.bukkit.towny.event.TownyLoadedDatabaseEvent;
import com.palmergames.bukkit.towny.event.time.NewShortTimeEvent;
import plugin.customresources.CustomResources;
import plugin.customresources.controllers.PlayerExtractionLimitsController;
import plugin.customresources.controllers.TownResourceProductionController;
import plugin.customresources.settings.CustomResourcesSettings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * 
 * @author Goosius
 *
 */
public class CustomResourcesTownyEventListener implements Listener {

	private static int PRODUCTION_RECALCULATION_INTERVAL_MILLIS = 600000; //10 mins
	private static long nextProductionRecalculationTime = 0; //0 so that it recalculates immediately on the 1st short tick

	/**
     * Whe the Towny database gets reloaded, Townyresources reloads also.
     */
    @EventHandler
    public void onTownyDatabaseLoad(TownyLoadedDatabaseEvent event) {
        if(CustomResourcesSettings.isEnabled()) {
            CustomResources.info("Towny database reload detected, reloading customresources...");
            CustomResources.getPlugin().reloadAll();
        }
    }

    /**
     * On Towny new day, town resources are automatically extracted.
     */
    @EventHandler
    public void onNewDay(PreNewDayEvent event) {
        if(CustomResourcesSettings.isEnabled()) {
            TownResourceProductionController.produceAllResources();
            PlayerExtractionLimitsController.resetDailyExtractionLimits();
        }
    }
       
    /**
     * On each ShortTime period, CustomResources saves data on player-extracted resources.
     * 
     * Every 10 mins, the produced town & nation resources are recalculated. 
     */
    @EventHandler
    public void onNewShortTime(NewShortTimeEvent event) {
        if(CustomResourcesSettings.isEnabled()) {
            PlayerExtractionLimitsController.resetMobsDamagedByPlayers();
            PlayerExtractionLimitsController.saveExtractionRecordsForOnlinePlayers();

            if(System.currentTimeMillis() > nextProductionRecalculationTime) {
                nextProductionRecalculationTime = System.currentTimeMillis() + PRODUCTION_RECALCULATION_INTERVAL_MILLIS; 
                TownResourceProductionController.recalculateAllProduction();
            }
        }
    }
}
