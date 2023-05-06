package plugin.customresources.listeners;

import com.palmergames.bukkit.towny.event.PreNewDayEvent;
import com.palmergames.bukkit.towny.event.TownyLoadedDatabaseEvent;
import com.palmergames.bukkit.towny.event.time.NewShortTimeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import plugin.customresources.CustomResources;
import plugin.customresources.controllers.TownMachineManager;
import plugin.customresources.controllers.TownResourceProductionController;
import plugin.customresources.settings.CustomResourcesSettings;

import static plugin.customresources.settings.MachineDataHandler.saveMachines;

/**
 *
 * @author Goosius
 *
 */
public class CustomResourcesTownyEventListener implements Listener {

	private static final int PRODUCTION_RECALCULATION_INTERVAL_MILLIS = 600000; //10 mins
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
            TownMachineManager.damageMachines();
            TownMachineManager.repairMachines();
            TownMachineManager.upgradeMachines();

            TownResourceProductionController.produceAllResources();
            TownMachineManager.machineGenerateResources();
        }
    }

    /**
     * On each ShortTime period, CustomResources saves data on player-extracted resources.
     *
     */
    @EventHandler
    public void onNewShortTime(NewShortTimeEvent event) {
            if(System.currentTimeMillis() > nextProductionRecalculationTime) {
                nextProductionRecalculationTime = System.currentTimeMillis() + PRODUCTION_RECALCULATION_INTERVAL_MILLIS;
                saveMachines();
                TownResourceProductionController.recalculateAllProduction();
            }
    }
}
