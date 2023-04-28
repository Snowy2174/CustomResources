package plugin.customresources.listeners;

import com.palmergames.bukkit.towny.event.PreNewDayEvent;
import com.palmergames.bukkit.towny.event.TownyLoadedDatabaseEvent;
import com.palmergames.bukkit.towny.event.time.NewShortTimeEvent;
import plugin.customresources.TownyResources;
import plugin.customresources.controllers.PlayerExtractionLimitsController;
import plugin.customresources.controllers.TownResourceProductionController;
import plugin.customresources.settings.TownyResourcesSettings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * 
 * @author Goosius
 *
 */
public class TownyResourcesTownyEventListener implements Listener {

	private static int PRODUCTION_RECALCULATION_INTERVAL_MILLIS = 600000; //10 mins
	private static long nextProductionRecalculationTime = 0; //0 so that it recalculates immediately on the 1st short tick

	/**
     * Whe the Towny database gets reloaded, Townyresources reloads also.
     */
    @EventHandler
    public void onTownyDatabaseLoad(TownyLoadedDatabaseEvent event) {
        if(TownyResourcesSettings.isEnabled()) {
            TownyResources.info("Towny database reload detected, reloading townyresources...");
            TownyResources.getPlugin().reloadAll();
        }
    }

    /**
     * On Towny new day, town resources are automatically extracted.
     */
    @EventHandler
    public void onNewDay(PreNewDayEvent event) {
        if(TownyResourcesSettings.isEnabled()) {
            TownResourceProductionController.produceAllResources();
            PlayerExtractionLimitsController.resetDailyExtractionLimits();
        }
    }
       
    /**
     * On each ShortTime period, TownyResources saves data on player-extracted resources.
     * 
     * Every 10 mins, the produced town & nation resources are recalculated. 
     */
    @EventHandler
    public void onNewShortTime(NewShortTimeEvent event) {
        if(TownyResourcesSettings.isEnabled()) {
            PlayerExtractionLimitsController.resetMobsDamagedByPlayers();
            PlayerExtractionLimitsController.saveExtractionRecordsForOnlinePlayers();

            if(System.currentTimeMillis() > nextProductionRecalculationTime) {
                nextProductionRecalculationTime = System.currentTimeMillis() + PRODUCTION_RECALCULATION_INTERVAL_MILLIS; 
                TownResourceProductionController.recalculateAllProduction();
            }
        }
    }
}
