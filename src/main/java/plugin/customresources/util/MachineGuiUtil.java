package plugin.customresources.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.customresources.objects.Machine;
import plugin.customresources.objects.MachineConfig;

import javax.swing.plaf.ColorUIResource;
import java.util.Arrays;

public class MachineGuiUtil {
    // Main interface inventory
    public static Inventory inventory;

    // Create new GUI inventory
    public static void createMachineInterface(Player player, Machine machine){
        String type = machine.getType();
        String state = String.valueOf(machine.getState());
        Integer tier = machine.getTier();

        String title = ("&a" + state + " &7| " + type + " | " + " [&6 " + tier + " &7]");

        inventory = Bukkit.createInventory(null, 36, title);
        populateMachineInterface(inventory);
        openInventory(player);
    }

    // Fill interface with items
    private static void populateMachineInterface(Inventory inventory){
        inventory.addItem(createGuiItem(Material.DIAMOND_SWORD, "Example Item", "§aFirst line of the lore", "§bSecond line of the lore"));
        // populate inventory with items
    }

    protected static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static void openInventory(final HumanEntity ent) {
        ent.openInventory(inventory);
    }

    // On click inside inventory, perform action
    public static void onInterfaceInteract(InventoryClickEvent event){
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        Player player = (Player) event.getWhoClicked();

        player.sendMessage("You clicked at slot " + event.getRawSlot());
    }

    // On click inside own inventory, cancel event
}
