package plugin.customresources.listeners;

import com.palmergames.adventure.text.Component;
import com.palmergames.bukkit.towny.event.statusscreen.NationStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Translator;
import com.palmergames.bukkit.towny.utils.TownyComponents;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * 
 * @author Goosius
 *
 */
public class CustomResourcesNationEventListener implements Listener {

	/*
	 * CustomResources will add resource info to the town screen
	 */
	@EventHandler
	public void onNationStatusScreen(NationStatusScreenEvent event) {
		if (CustomResourcesSettings.isEnabled()) {
			Translator translator = Translator.locale(event.getCommandSender());
			Nation nation = event.getNation();
			String productionAsString = CustomResourcesGovernmentMetaDataController.getDailyProduction(nation);
			String availableAsString = CustomResourcesGovernmentMetaDataController.getAvailableForCollection(nation);

			if(productionAsString.isEmpty() && availableAsString.isEmpty())
				return;

			//Resources:
			Component component = Component.empty();
			component = component.append(Component.newline());
			component = component.append(TownyComponents.legacy(translator.of("customresources.nation.screen.header"))).append(Component.newline());
		
			// > Daily Productivity [2]: 32 oak Log, 32 sugar cane
			component = component.append(CustomResourcesMessagingUtil.getSubComponentForGovernmentScreens(translator, productionAsString, "customresources.nation.screen.daily.production")).append(Component.newline());
			
			// > Available For Collection [2]: 64 oak log, 64 sugar cane
			component = component.append(CustomResourcesMessagingUtil.getSubComponentForGovernmentScreens(translator, availableAsString, "customresources.nation.screen.available.for.collection")).append(Component.newline());
			event.getStatusScreen().addComponentOf("CustomResources", component);
		}
	}
}
