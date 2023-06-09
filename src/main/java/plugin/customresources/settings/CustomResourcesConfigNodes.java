package plugin.customresources.settings;

public enum CustomResourcesConfigNodes {

	VERSION_HEADER("version", "", ""),
	VERSION(
			"version.version",
			"",
			"# This is the current version.  Please do not edit."),
	ENABLED(
			"enabled",
			"true",
			"",
			"# If true, the CustomResources system is enabled.",
			"# if false, the CustomResources system is disabled."),
	TOWN_RESOURCES(
			"town_resources",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                  Town Resources                      | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	TOWN_RESOURCES_SURVEYS(
			"town_resources.surveys",
			"",
			"",
			""),
	TOWN_RESOURCES_SURVEYS_ENABLED(
			"town_resources.surveys.enabled",
			"true",
			"",
			"# If true, players can do surveys.",
			"# if false, they cannot."),
	TOWN_RESOURCES_MACHINERY_LEVEL(
			"town_resources.machinery_level.levels",
			"1, 2, 3, 4",
			"",
			"# List of values that determine the values of machinery levels all towns can have.",
			"# Must be in ascending order and cannot have gaps (e.g. '1, 2, 4' would not work)."),
	TOWN_RESOURCES_COST_PER_MACHINERY_LEVEL(
			"town_resources.machinery_level.cost_per_level",
			"100.00, 200.00, 300.00, 400.00",
			"",
			"# List of values that determine how much money a town needs to upgrade its machinery level"),
	TOWN_RESOURCES_TOWN_BLOCK_PER_MACHINERY_LEVEL(
			"town_resources.machinery_level.town_block_per_level",
			"10, 50, 120",
			"",
			"# List of values that determines how many town blocks a town must have in order to upgrade its machinery level"),
	TOWN_RESOURCES_PRODUCTION(
			"town_resources.production",
			"",
			"",
			""),
	TOWN_RESOURCES_PRODUCTION_ENABLED(
			"town_resources.production.enabled",
			"true",
			"",
			"# If true, towns produce resources.",
			"# if false, towns do not produce resource."),
	TOWN_RESOURCES_PRODUCTION_TOWN_LEVEL_REQUIREMENT_PER_RESOURCE_LEVEL(
			"town_resources.production.town_level_requirement_per_resource_level",
			"2, 4, 6, 8",
			"",
			"# This list of values determines the level a town needs to be, to PRODUCE its resources.",
			"# Example: If the first value is 1, then the town has to be level 1 to produce its level 1 resource."),
	TOWN_RESOURCES_PRODUCTION_PRODUCTIVITY_PERCENTAGE_PER_RESOURCE_LEVEL(
			"town_resources.production.productivity_percentage_per_resource_level",
			"100, 200, 300, 400",
			"",
			"# This list of values determines the productivity of each level of resource.",
			"# Example: If the second value is 200, then the level 2 resource of a town will be 200% productive."),
	TOWN_RESOURCES_PRODUCTION_STORAGE_LIMIT_MODIFIER(
			"town_resources.production.storage_limit_modifier",
			"5",
			"",
			"# This value determines the limit of how many resources of each type can be stored for collection.",
			"# Example: If this value 3, and the daily production amount is 32 ..... then the storage limit is 96."),
	TOWN_RESOURCES_PRODUCTION_NATION_TAX_PERCENTAGE(
			"town_resources.production.nation_tax_percentage",
			"50",
			"",
			"# The nation of a town gets this percentage of town production, as long as the town is not occupied.",
			"# The town gets the rest."),
	TOWN_RESOURCES_PRODUCTION_OCCUPYING_NATION_TAX_PERCENTAGE(
			"town_resources.production.occupying_nation_tax_percentage",
			"50",
			"",
			"# If a town is occupied, the occupying nation gets this percentage of town production.",
			"# The town gets the rest."),
	TOWN_RESOURCES_OFFERS(
			"town_resources.offers",
			"",
			"",
			""),
	TOWN_RESOURCES_OFFERS_CATEGORIES(
			"town_resources.offers.categories",
			"" +
			"{common_dirt, 25, 2, DIRT}," +
			"{gravel, 100, 1, GRAVEL}," +
			"{sand, 100, 2, SAND}," +
			"{common_rocks, 100, 2, STONE, COBBLESTONE}," +
			"{uncommon_rocks, 100, 1, DIORITE, ANDESITE, GRANITE}," +
			"{terracotta, 100, 0.5, TERRACOTTA}," +
			"{quartz, 100, 0.25, QUARTZ_BLOCK}," +
			"{prismarine, 100, 0.125, PRISMARINE}," +
			"{obsidian, 100, 0.125, OBSIDIAN}," +
			"{clay, 100, 1, CLAY_BALL}," +
			"{ice, 25, 1, ICE}," +
			"{snow, 25, 1, SNOWBALL}," +
			"{coal, 200, 1, COAL}," +
			"{iron, 200, 0.5, RAW_IRON}," +
			"{gold, 200, 0.25, RAW_GOLD}," +
			"{copper, 100, 0.25, RAW_COPPER}," +
			"{emeralds, 200, 0.25, EMERALD}," +
			"{diamonds, 200, 0.125, DIAMOND}," +
			"{redstone, 100, 1, REDSTONE}," +
			"{ancient_debris, 200, 0.03125, ANCIENT_DEBRIS}," +
			"{wood, 200, 2, OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, " +
			"OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK_WOOD}," +
			"{wheat, 100, 1, WHEAT}," +
			"{carrots, 100, 1, CARROT}," +
			"{potatoes, 100, 1, POTATO}," +
			"{beetroots, 100, 1, BEETROOT}," +
			"{pumpkins, 100, 1, PUMPKIN_SEEDS}," +
			"{melon_slices, 100, 1, MELON_SLICE}," +
			"{cocoa_plants, 100, 1, COCOA_BEANS}," +
			"{kelp, 100, 1, KELP}," +
			"{bamboo, 100, 1, BAMBOO}," +
			"{cactus, 100, 1, CACTUS}," +
			"{mushrooms, 100, 1, BROWN_MUSHROOM, RED_MUSHROOM}," +
			"{apple_trees, 100, 0.125, APPLE}," +
			"{vines, 100, 0.25, VINE}," +
			"{fish, 100, 1, COD, SALMON, PUFFERFISH, TROPICAL_FISH}," +
			"{wool, 100, 0.5, WHITE_WOOL}," +
			"{gunpowder, 200, 0.25, GUNPOWDER}," +
			"{string, 100, 0.25, STRING}," +
			"{spider_eyes, 100, 0.25, SPIDER_EYE}," +
			"{slime, 100, 0.25, SLIME_BALL}," +
			"{feathers, 100, 0.25, FEATHER}," +
			"{leather, 100, 0.25, LEATHER}," +
			"{rabbit_hide, 100, 0.25, RABBIT_HIDE}," +
			"{rotten_flesh, 100, 1, ROTTEN_FLESH}" +
			"{bones, 100, 0.5, BONE}," +
			"{pork, 100, 0.25, PORKCHOP}," +
			"{mutton, 100, 0.25, MUTTON}," +
			"{beef, 100, 0.25, BEEF}," +
			"{chickens, 100, 0.25, CHICKEN}," +
			"{ender_pearls, 100, 0.125, ENDER_PEARL}",
		   "",
			"# This list shows the survey offer categories.",
			"# ",
			"# Each category is enclosed in curly brackets, and has 4 parts:",
			"# 1 - The name of the category (used for messaging)",
			"# 2 - The discovery weight of the category (used during discovery)",
			"# 3-  The base amount of the offer (in stacks)",
			"# 4-  The list of materials in the category",
			"# ",
			"# The default values give a weight of 200 to strategic resources (coal, iron etc.), and 100 to most non-strategic resources. (wheat, quartz etc.)",
			"# This gives each survey approx. 70% chance to discover a strategic resources, and a 30% to discover a non-strategic resource.",
			"# ",
			"# This list supports Slimefun, MythicMobs and MMOItems items as well. When entering an MMOItem use the TYPE:ID format,",
			"# ie: SWORD:CUTLASS where CustomResources expects a material."),
	TOWN_RESOURCES_OFFERS_MATERIALS_WITH_NON_DYNAMIC_AMMOUNTS(
			"town_resources.offers.categories_with_fixed_amounts",
			"somematerial,someitem",
			"",
			"# This list is used for Offers materials which you do not want to have dynamic amounts set.",
			"# Ie: the amounts that are set in the above offers.categories sections are exactly how many each town will receive, every time.",
			"# Materials put into this list, will not have their amounts modified by the Town's Town_Level resourceProductionModifier (in the Towny config,)",
			"# or by the productivity_percentage_per_resource_level settings in this config.",
			"# You may find it useful for controlling very valuable materials or custom items from Slimefun, MythicMobs or MMOItems."),
	TOWN_RESOURCES_LANGUAGE(
			"town_resources.language",
			"",
			"",
			""),
	TOWN_RESOURCES_LANGUAGE_MATERIALS_DISPLAY_LANGUAGE(
			"town_resources.language.materials_display_language",
			"zh_cn",
			"",
			"# If you have the LanguageUtils plugin installed, materials will be automatically translated into this locale/language.");
	private final String Root;
	private final String Default;
	private final String[] comments;

	CustomResourcesConfigNodes(String root, String def, String... comments) {

		this.Root = root;
		this.Default = def;
		this.comments = comments;
	}

	/**
	 * Retrieves the root for a config option
	 *
	 * @return The root for a config option
	 */
	public String getRoot() {

		return Root;
	}

	/**
	 * Retrieves the default value for a config path
	 *
	 * @return The default value for a config path
	 */
	public String getDefault() {

		return Default;
	}

	/**
	 * Retrieves the comment for a config path
	 *
	 * @return The comments for a config path
	 */
	public String[] getComments() {

		if (comments != null) {
			return comments;
		}

		String[] comments = new String[1];
		comments[0] = "";
		return comments;
	}

}
