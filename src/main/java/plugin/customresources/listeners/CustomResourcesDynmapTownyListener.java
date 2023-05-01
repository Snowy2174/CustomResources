package plugin.customresources.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.towny.events.BuildTownMarkerDescriptionEvent;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

public class CustomResourcesDynmapTownyListener implements Listener {

    /**
     * This method updates the town popup box on Dynmap-Towny
     *
     * 1. It looks for the %town_resources% tag in the popup
     * 2. If the %town_resouces% tag exists, it replaces it with a list of town resources.
     */
    @EventHandler
    public void on(BuildTownMarkerDescriptionEvent event) {
        if (CustomResourcesSettings.isEnabled()) {
            if (event.getDescription().contains("%town_resources%")) {
                String productionAsString = CustomResourcesGovernmentMetaDataController.getDailyProduction(event.getTown());
                productionAsString = CustomResourcesMessagingUtil.adjustAmountsForTownLevelModifier(event.getTown(), productionAsString);
                String formattedProductionAsString = CustomResourcesMessagingUtil.formatProductionStringForDynmapTownyDisplay(productionAsString);
                String finalDescription = event.getDescription().replace("%town_resources%", formattedProductionAsString);
                event.setDescription(finalDescription);
            }
        }
    }
}
