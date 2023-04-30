package plugin.customresources.controllers;

import com.gmail.goosius.siegewar.TownOccupationController;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.CustomResources;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.objects.MachineConfig;
import plugin.customresources.objects.MachineTier;
import plugin.customresources.settings.CustomResourceMachineConfig;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.util.ArrayList;
import java.util.List;

import static plugin.customresources.settings.CustomResourceMachineConfig.MACHINES;

public class TownResourceDiscoveryController {
    /**
     * Discover a new resource for a town
     * 
     * After discovery, recalculates town production
     * After discovery, recalculates nation production (if the town has an owner nation)
     * 
     * @param resident the resident who did the survey
     * @param town the town
     * @param machine the machine that is constructed
     * @param surveyCost the cost of the survey
     * @param alreadyDiscoveredMaterials list of the town's already-discovered materials
     * @throws TownyException 
     */
    public static void discoverNewResource(Resident resident,
                                           Town town,
                                           String machine,
                                           int surveyLevel,
                                           double surveyCost,
                                           List<String> alreadyDiscoveredMaterials) throws TownyException{

        //Ensure the resource at this level has not already been discovered
        List<String> discoveredResources = CustomResourcesGovernmentMetaDataController.getDiscoveredAsList(town);
        if(surveyLevel <= discoveredResources.size()) {
            throw new TownyException(Translatable.of("customresources.msg_err_level_x_resource_already_discovered", surveyLevel));
        }

        //Ensure the player can afford this survey
        if (TownyEconomyHandler.isActive()) {
            if(!resident.getAccount().canPayFromHoldings(surveyCost)) {
			    throw new TownyException(Translatable.of("customresources.msg_err_survey_too_expensive",
                    TownyEconomyHandler.getFormattedBalance(surveyCost), resident.getAccount().getHoldingFormattedBalance()));
            }

            //Pay for the survey
            resident.getAccount().withdraw(surveyCost, "Cost of resources survey.");
        }

        //Calculate a new category and material for discovery
        List<String> discoveredMaterials = new ArrayList<>(alreadyDiscoveredMaterials);
        //Discover the resource
        String material = MACHINES.get(machine).getTiers().get(0).getOutputMaterials().get(0);
        discoveredMaterials.add(material);
        CustomResourcesGovernmentMetaDataController.setDiscovered(town, discoveredMaterials);
        town.save();

        //Recalculate Town Production
        TownResourceProductionController.recalculateProductionForOneTown(town);

        //Recalculate Nation Production
        if(CustomResources.getPlugin().isSiegeWarInstalled() && TownOccupationController.isTownOccupied(town)) {
            TownResourceProductionController.recalculateProductionForOneNation(TownOccupationController.getTownOccupier(town));
        } else if (town.hasNation()) {
            TownResourceProductionController.recalculateProductionForOneNation(town.getNation());
        }

         //Send global message
        int levelOfNewResource = discoveredMaterials.size();
        double productivityModifierNormalized;
        productivityModifierNormalized = (double) CustomResourcesSettings.getProductionPercentagesPerResourceLevel().get(levelOfNewResource - 1) / 100;
        int preTaxProduction = (int)((MACHINES.get(machine).getTiers().get(0).getOutputAmounts().get(0) * productivityModifierNormalized) + 0.5);
        String materialName = CustomResourcesMessagingUtil.formatMaterialNameForDisplay(material);
        CustomResourcesMessagingUtil.sendGlobalMessage(Translatable.of("customresources.discovery.success", resident.getName(), town.getName(), preTaxProduction, materialName));
    }
}
