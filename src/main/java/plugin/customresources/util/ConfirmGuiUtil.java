package plugin.customresources.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.objects.Machine;

import static plugin.customresources.util.MachineGuiUtil.createGuiItem;

public class ConfirmGuiUtil {

    public static Inventory confirmInventory;
    public enum ConfirmationAction {
        UPGRADE("Confirm Upgrade", Material.ANVIL, ChatColor.GREEN),
        DESTROY("Confirm Destroy", Material.REDSTONE_BLOCK, ChatColor.RED);

        private final String name;
        private final Material material;
        private final ChatColor color;

        ConfirmationAction(String name, Material material, ChatColor color) {
            this.name = name;
            this.material = material;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Material getMaterial() {
            return material;
        }

        public ChatColor getColor() {
            return color;
        }
    }

    public static void openConfirmation(ConfirmationAction action, Player player, Machine machine) {
        final int CONFIRM_SLOT = 11;
        final int CANCEL_SLOT = 15;

        confirmInventory = Bukkit.createInventory(null, 36, "Confirm " + action.getName());

        ItemStack confirmItem = createGuiItem((action.getMaterial()), (action.getColor() + action.getName()), machine, "" );
        ItemStack cancelItem = createGuiItem((Material.EMERALD_BLOCK), (ChatColor.RED + "Cancel"), machine, "" );

        confirmInventory.setItem(CONFIRM_SLOT, confirmItem);
        confirmInventory.setItem(CANCEL_SLOT, cancelItem);

        player.openInventory(confirmInventory);
    }

    public static void onConfirmationInteract(InventoryClickEvent event){
        if (event.getInventory().equals(confirmInventory)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType().isAir()) return;

            ConfirmGuiUtil.ConfirmationAction action = ConfirmGuiUtil.ConfirmationAction.valueOf(ChatColor.stripColor((event.getView().getTitle()).replace("Confirm ", "").toUpperCase()));

            ConfirmGuiUtil.handleConfirmationResponse((Player) event.getWhoClicked(), clickedItem, action);
        }

    }


    public static void handleConfirmationResponse(Player player, ItemStack clickedItem, ConfirmationAction action) {
        if (clickedItem.getType() == action.getMaterial()) {
            if (action == ConfirmationAction.DESTROY) {
                // Code to destroy machine
            } else if (action == ConfirmationAction.UPGRADE) {
                // Code to upgrade machine
            }
        }

        player.closeInventory();
    }
}
