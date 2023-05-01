package plugin.customresources.enums;

/**
 *
 * @author Goosius
 *
 */
public enum CustomResourcesPermissionNodes {

	TOWNY_RESOURCES_COMMAND("customresources.command.*"),
	TOWNY_RESOURCES_COMMAND_SURVEY("customresources.command.survey"),  //Do a resource survey in a town
	TOWNY_RESOURCES_COMMAND_TOWN_COLLECT("customresources.command.towncollect"),  //Collect your town's share of extracted resources
	TOWNY_RESOURCES_COMMAND_NATION_COLLECT("customresources.command.nationcollect"), //Collect your nation's share of extracted resources
	TOWNY_RESOURCES_ADMIN_COMMAND("customresources.admin.command.*"),
	TOWNY_RESOURCES_ADMIN_COMMAND_RELOAD("customresources.admin.command.reload"),
	TOWNY_RESOURCES_ADMIN_COMMAND_BYPASS("customresources.admin.command.bypass"),
	TOWNY_RESOURCES_BYPASS("customresources.bypass"),
	CUSTOM_RESOURCES_SURVEY("customresources.construct");

	private final String value;

	/**
	 * Constructor
	 *
	 * @param permission - Permission.
	 */
	CustomResourcesPermissionNodes(String permission) {

		this.value = permission;
	}

	/**
	 * Retrieves the permission node
	 *
	 * @return The permission node
	 */
	public String getNode() {

		return value;
	}

	/**
	 * Retrieves the permission node
	 * replacing the character *
	 *
	 * @param replace - String
	 * @return The permission node
	 */
	public String getNode(String replace) {

		return value.replace("*", replace);
	}

	public String getNode(int replace) {

		return value.replace("*", replace + "");
	}

}
