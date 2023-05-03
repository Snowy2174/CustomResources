package plugin.customresources.listeners;

import com.palmergames.adventure.text.Component;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translator;
import com.palmergames.bukkit.towny.utils.TownyComponents;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Goosius
 *
 */
public class CustomResourcesTownEventListener implements Listener {

	@EventHandler
	public void onNewTown(NewTownEvent event){
		Town town = event.getTown();
		CustomResourcesGovernmentMetaDataController.checkTownMachineryLevel(town);
	}

	/*
	 * CustomResources will add resource info to the town screen
	 */
	@EventHandler
	public void onTownStatusScreen(TownStatusScreenEvent event) {
		if (CustomResourcesSettings.isEnabled()) {
			Translator translator = Translator.locale(event.getCommandSender());
			Town town = event.getTown();
			String productionAsString = CustomResourcesGovernmentMetaDataController.getDailyProduction(town);
			String availableAsString = CustomResourcesGovernmentMetaDataController.getAvailableForCollection(town);

			if(productionAsString.isEmpty() && availableAsString.isEmpty())
				return;

			productionAsString = CustomResourcesMessagingUtil.adjustAmountsForTownLevelModifier(town, productionAsString);

			//Resources:
			Component component = Component.empty();
			component = component.append(Component.newline());
			component = component.append(TownyComponents.legacy(translator.of("customresources.town.screen.header"))).appendNewline();

			// > Daily Productivity [2]: 32 oak Log, 32 sugar cane
			component = component.append(CustomResourcesMessagingUtil.getSubComponentForGovernmentScreens(translator, productionAsString, "customresources.town.screen.daily.production")).appendNewline();

			// > Available For Collection [2]: 64 oak log, 64 sugar cane
			component = component.append(CustomResourcesMessagingUtil.getSubComponentForGovernmentScreens(translator, availableAsString, "customresources.town.screen.available.for.collection")).appendNewline();
			// > TownLevel Modifier: +10%.
			if (TownySettings.getTownLevel(town).resourceProductionModifier() != 1.0)
				component = component.append(getTownModifierComponent(town, translator)).appendNewline();
			event.getStatusScreen().addComponentOf("CustomResources", component);
		}
	}

	private Component getTownModifierComponent(Town town, Translator translator) {
		double townModifier = TownySettings.getTownLevel(town).resourceProductionModifier();
		String modifierSlug = "";
		if (townModifier > 1.0)
			modifierSlug = "+" + BigDecimal.valueOf((townModifier - 1) * 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
		if (townModifier < 1.0)
			modifierSlug = String.valueOf(BigDecimal.valueOf((townModifier * 100) - 100).setScale(2, RoundingMode.HALF_UP).doubleValue());
		return Component.text(translator.of("customresources.town.screen.town.level.modifier", modifierSlug)).append(Component.text("%"));
	}
}
