package plugin.customresources.controllers;

import com.gmail.goosius.siegewar.TownOccupationController;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import plugin.customresources.CustomResources;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.objects.Machine;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.util.ArrayList;
import java.util.List;

import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;

public class TownResourceDiscoveryController {
    /**
     * Discover a new resource for a town
     * 
     * After discovery, recalculates town production
     * After discovery, recalculates nation production (if the town has an owner nation)
     *
     * @param machine the machine that is constructed
     * @throws TownyException 
     */
    public static void discoverNewResource(Machine machine) throws TownyException{

        Town town = WorldCoord.parseWorldCoord(machine.getLocation()).getTownBlock().getTown();
        List<String> alreadyDiscoveredMaterials = CustomResourcesGovernmentMetaDataController.getDiscoveredAsList(town);

        //Calculate a new category and material for discovery
        List<String> discoveredMaterials = new ArrayList<>(alreadyDiscoveredMaterials);
        //Discover the resource
        String material = machine.getTierConfig().getOutputMaterials().get(machine.getResourceType());
        discoveredMaterials.add(material);
        CustomResourcesGovernmentMetaDataController.setDiscovered(town, discoveredMaterials);
        town.save();

        //Recalculate Nation Production
        if(CustomResources.getPlugin().isSiegeWarInstalled() && TownOccupationController.isTownOccupied(town)) {
            TownResourceProductionController.recalculateProductionForOneNation(TownOccupationController.getTownOccupier(town));
        } else if (town.hasNation()) {
            TownResourceProductionController.recalculateProductionForOneNation(town.getNation());
        }

         //Send global message
        double preTaxProduction = machine.getTierConfig().getOutputAmounts().get(machine.getResourceType());
        String materialName = CustomResourcesMessagingUtil.formatMaterialNameForDisplay(material);
        CustomResourcesMessagingUtil.sendGlobalMessage(Translatable.of("customresources.discovery.success", town.getName(),  materialName, preTaxProduction));
    }

    public static void removeResource(Machine machine) throws TownyException{

        Town town = TownyAPI.getInstance().getTown(machine.getLocation());
        List<String> alreadyDiscoveredMaterials = CustomResourcesGovernmentMetaDataController.getDiscoveredAsList(town);

        //Calculate a new category and material for discovery
        List<String> discoveredMaterials = new ArrayList<>(alreadyDiscoveredMaterials);
        //Discover the resource
        String material = machine.getTierConfig().getOutputMaterials().get(machine.getResourceType());
        discoveredMaterials.remove(material);
        CustomResourcesGovernmentMetaDataController.setDiscovered(town, discoveredMaterials);
        town.save();

        //Recalculate Nation Production
        if(CustomResources.getPlugin().isSiegeWarInstalled() && TownOccupationController.isTownOccupied(town)) {
            TownResourceProductionController.recalculateProductionForOneNation(TownOccupationController.getTownOccupier(town));
        } else if (town.hasNation()) {
            TownResourceProductionController.recalculateProductionForOneNation(town.getNation());
        }
    }

    public static void reloadResource(Machine machine) throws TownyException {
        removeResource(machine);
        discoverNewResource(machine);
    }
}
