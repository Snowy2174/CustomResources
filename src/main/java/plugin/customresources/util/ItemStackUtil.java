package plugin.customresources.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;

import java.util.Arrays;

public class ItemStackUtil {

    public static ItemStack createMachineIcon(final Material material, final String name, Machine machine, final String... lore) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        meta.getPersistentDataContainer().set(new NamespacedKey(CustomResources.getPlugin(), "machineId"),
                PersistentDataType.STRING,
                machine.getId().toString());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);
        return item;
    }



}
