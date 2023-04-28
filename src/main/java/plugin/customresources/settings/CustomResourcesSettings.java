package plugin.customresources.settings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.palmergames.bukkit.config.CommentedConfiguration;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.util.FileMgmt;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.ResourceOfferCategory;
import plugin.customresources.util.MMOItemsUtil;
import plugin.customresources.util.MythicMobsUtil;

import org.bukkit.Material;

public class CustomResourcesSettings {
	private static CommentedConfiguration config, newConfig;
	private static int sumOfAllOfferDiscoveryProbabilityWeights = 0;  //Used when getting the resource offers
	private static Path configPath = CustomResources.getPlugin().getDataFolder().toPath().resolve("config.yml");

	public static boolean isEnabled() {
		return getBoolean(CustomResourcesConfigNodes.ENABLED);
	}

    public static List<Integer> getSurveyCostsPerResourceLevel() {
	    return getIntegerList(CustomResourcesConfigNodes.TOWN_RESOURCES_SURVEYS_COST_PER_RESOURCE_LEVEL);
	}

    public static List<Integer> getSurveyNumTownblocksRequirementsPerResourceLevel() {
    	return getIntegerList(CustomResourcesConfigNodes.TOWN_RESOURCES_SURVEYS_NUM_TOWNBLOCKS_REQUIREMENT_PER_RESOURCE_LEVEL);
	}
		
	public static List<Integer> getProductionPercentagesPerResourceLevel() {
		return getIntegerList(CustomResourcesConfigNodes.TOWN_RESOURCES_PRODUCTION_PRODUCTIVITY_PERCENTAGE_PER_RESOURCE_LEVEL);
	}
	
	public static List<Integer> getProductionTownLevelRequirementPerResourceLevel() {
		return getIntegerList(CustomResourcesConfigNodes.TOWN_RESOURCES_PRODUCTION_TOWN_LEVEL_REQUIREMENT_PER_RESOURCE_LEVEL);
	}
		
	public static double getTownResourcesProductionNationTaxNormalized() {
		return getDouble(CustomResourcesConfigNodes.TOWN_RESOURCES_PRODUCTION_NATION_TAX_PERCENTAGE) / 100;
	}	
	
	public static double getTownResourcesProductionOccupyingNationTaxNormalized() {
		return getDouble(CustomResourcesConfigNodes.TOWN_RESOURCES_PRODUCTION_OCCUPYING_NATION_TAX_PERCENTAGE) / 100;
	}

	public static int getSumOfAllOfferDiscoveryProbabilityWeights() {
		return sumOfAllOfferDiscoveryProbabilityWeights;
	}

	/**
	 * Get all resource offer categories
	 * 
	 * @return a list of all resource offer categories
	 * @throws TownyException a towny exception
	 */
	public static List<ResourceOfferCategory> getResourceOfferCategories() throws TownyException{
		List<ResourceOfferCategory> result = new ArrayList<>();
		boolean problemLoadingCategories = false;

		String categoriesAsString = getString(CustomResourcesConfigNodes.TOWN_RESOURCES_OFFERS_CATEGORIES);
		
		if(!categoriesAsString.isEmpty()) {		
			Pattern pattern = Pattern.compile("\\{([^}]+)}", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(categoriesAsString);
			String categoryAsString;
			String[] categoryAsArray;
			String categoryName;
			int categoryDiscoveryWeight;
			double categoryBaseAmountStacks;
			int categoryBaseAmountItems;
			List<String> materialsInCategory;
			String materialName;
			ResourceOfferCategory resourceOfferCategory;
			
			while (matcher.find()) {
				//Read one resource offer category
				categoryAsString = matcher.group(1);
				   
				categoryAsArray = categoryAsString.split(",");
				if(categoryAsArray.length < 2) {
					CustomResources.severe("Bad configuration for offer category: " + categoryAsString);
					problemLoadingCategories = true;
					continue;
				}
				
				//Read name
				categoryName = categoryAsArray[0].trim();
				
				//Read disovery weight
				categoryDiscoveryWeight = Integer.parseInt(categoryAsArray[1].trim());
				
				//Read base amount
				categoryBaseAmountStacks = Double.parseDouble(categoryAsArray[2].trim());
				categoryBaseAmountItems = (int)((categoryBaseAmountStacks * 64) + 0.5);
				
				//Read Materials
				materialsInCategory = new ArrayList<>();
				for(int i = 3; i < categoryAsArray.length; i++) {
					materialName = categoryAsArray[i].trim();
					if(!isValidMaterial(materialName)) {
						CustomResources.severe("Unknown material in offer category. Category: " + categoryName + ". Material: " + categoryAsArray[i]);
						problemLoadingCategories = true;
						continue;
					}
					materialsInCategory.add(materialName);
				}
				
				//Construct ResourceExtractionCategory object
				resourceOfferCategory = new ResourceOfferCategory(categoryName, categoryDiscoveryWeight, categoryBaseAmountItems, materialsInCategory);
				
				//Add to result
				result.add(resourceOfferCategory);
			}
		}

		if(problemLoadingCategories) {
			throw new TownyException("Problem Loading Resource Offers");
		} else {
			return result;
		}
	}
	
	private static boolean isValidMaterial(String materialName) {
		Material material = Material.getMaterial(materialName);
		if(material != null)
			return true;  	//Known material
		if(CustomResources.getPlugin().isSlimeFunInstalled()) {
			SlimefunItem slimeFunItem = SlimefunItem.getById(materialName);
			if(slimeFunItem != null)
				return true;  //Known material 
		}
		// mythicmobs integration
		if (CustomResources.getPlugin().isMythicMobsInstalled()
		&& MythicMobsUtil.isValidItem(materialName))
			return true;
		
		// MMOItems integration
		if (CustomResources.getPlugin().isMMOItemsInstalled()
		&& materialName.contains(":")
		&& MMOItemsUtil.isValidItem(materialName))
			return true;

		return false; //Unknown material		
	}
	
	public static void loadConfig() throws TownyException {
		if (!FileMgmt.checkOrCreateFile(configPath.toString())) {
			throw new TownyException("Failed to create config file!");
		}
		
		config = new CommentedConfiguration(configPath, CustomResources.getPlugin());
		if (!config.load())
			throw new TownyException("Failed to load Config!");

		setDefaults(CustomResources.getPlugin().getVersion(), configPath);
		config.save();
	}

	public static void addComment(String root, String... comments) {
		newConfig.addComment(root.toLowerCase(), comments);
	}

	private static void setNewProperty(String root, Object value) {
		if (value == null) {
			value = "";
		}
		newConfig.set(root.toLowerCase(), value.toString());
	}

	@SuppressWarnings("unused")
	private static void setProperty(String root, Object value) {
		config.set(root.toLowerCase(), value.toString());
	}
	
	/**
	 * Builds a new config reading old config data.
	 */
	private static void setDefaults(String version, Path configPath) {
		newConfig = new CommentedConfiguration(configPath, CustomResources.getPlugin());
		newConfig.load();

		for (CustomResourcesConfigNodes root : CustomResourcesConfigNodes.values()) {
			if (root.getComments().length > 0)
				addComment(root.getRoot(), root.getComments());
			if (root.getRoot() == CustomResourcesConfigNodes.VERSION.getRoot())
				setNewProperty(root.getRoot(), version);
			else
				setNewProperty(root.getRoot(), (config.get(root.getRoot().toLowerCase()) != null) ? config.get(root.getRoot().toLowerCase()) : root.getDefault());
		}

		config = newConfig;
		newConfig = null;
	}
	
	public static String getString(String root, String def) {
		String data = config.getString(root.toLowerCase(), def);
		if (data == null) {
			sendError(root.toLowerCase() + " from config.yml");
			return "";
		}
		return data;
	}

	private static void sendError(String msg) {
		CustomResources.severe("Error could not read " + msg);
	}
	
	public static boolean getBoolean(CustomResourcesConfigNodes node) {
		return Boolean.parseBoolean(config.getString(node.getRoot().toLowerCase(), node.getDefault()));
	}

	public static double getDouble(CustomResourcesConfigNodes node) {
		try {
			return Double.parseDouble(config.getString(node.getRoot().toLowerCase(), node.getDefault()).trim());
		} catch (NumberFormatException e) {
			sendError(node.getRoot().toLowerCase() + " from config.yml");
			return 0.0;
		}
	}

	public static int getInt(CustomResourcesConfigNodes node) {
		try {
			return Integer.parseInt(config.getString(node.getRoot().toLowerCase(), node.getDefault()).trim());
		} catch (NumberFormatException e) {
			sendError(node.getRoot().toLowerCase() + " from config.yml");
			return 0;
		}
	}

	public static String getString(CustomResourcesConfigNodes node) {
		return config.getString(node.getRoot().toLowerCase(), node.getDefault());
	}

	private static List<String> getStrArr(CustomResourcesConfigNodes node) {
		return Arrays.stream(getString(node).split(",")).collect(Collectors.toList());
	}
	
	/**
     * Return an IMMUTABLE list of integers
	 */
	public static List<Integer> getIntegerList(CustomResourcesConfigNodes configEntry) {
		String configAsString = getString(configEntry);
		if(configAsString.isEmpty()) {
            return Collections.emptyList();
		} else {
			configAsString = configAsString.replaceAll(" ","");
			List<Integer> result = new ArrayList<>();
			for(String configValue: configAsString.split(",")) {
				result.add(Integer.parseInt(configValue));
			}
			return result;
		}    	
	}
	
	/**
     * Return an IMMUTABLE list of strings
	 */
	public static List<String> getStringList(CustomResourcesConfigNodes configEntry) {
		String configAsString = getString(configEntry);
		if(configAsString.isEmpty()) {
            return Collections.emptyList();
		} else {
			configAsString = configAsString.replaceAll(" ","");
			String[] configAsArray = configAsString.split(",");
			return Arrays.asList(configAsArray);
		}    	
	}

	public static boolean areSurveysEnabled() {
		return getBoolean(CustomResourcesConfigNodes.TOWN_RESOURCES_SURVEYS_ENABLED);
	}

	public static int getStorageLimitModifier() {
		return getInt(CustomResourcesConfigNodes.TOWN_RESOURCES_PRODUCTION_STORAGE_LIMIT_MODIFIER);
	}

	public static List<Double> getNormalizedProductionBonusesPerResourceLevel() {
		List<Integer> percentageBonuses = getProductionPercentagesPerResourceLevel();
		List<Double> normalizedBonuses = new ArrayList<>();
		for(Integer percentageBonus: percentageBonuses) {
			normalizedBonuses.add((double)percentageBonus / 100);
		}
		return normalizedBonuses;
	}

	public static boolean isNonDynamicAmountMaterial(String material) {
		return getStrArr(CustomResourcesConfigNodes.TOWN_RESOURCES_OFFERS_MATERIALS_WITH_NON_DYNAMIC_AMMOUNTS).contains(material);
	}
	
	public static String getMaterialsDisplayLanguage() {
		return getString(CustomResourcesConfigNodes.TOWN_RESOURCES_LANGUAGE_MATERIALS_DISPLAY_LANGUAGE);
	}

	public static boolean areMMOItemsGivenLeveledTowardsThePlayer() {
		return false; //getBoolean(CustomResourcesConfigNodes.TOWN_RESOURCES_OFFERS_MMOITEMS_PLAYER_LEVELED_ITEMS);
	}
}
