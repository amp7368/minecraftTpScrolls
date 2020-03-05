package apple;

import apple.listeners.InventoryChest;
import apple.utils.YMLNavigate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScrollInventories {

    public static Inventory scrollInvAll;
    public static Inventory scrollInvAllEdit;
    private static JavaPlugin plugin;

    public ScrollInventories(JavaPlugin pl) {
        plugin = pl;
        update();
    }

    public static void update() {
        scrollInvAll = Bukkit.createInventory(new InventoryChest(54, "Scrolls"), InventoryType.CHEST);
        scrollInvAllEdit = Bukkit.createInventory(new InventoryChest(54, "ScrollsEdit"), InventoryType.CHEST);
        File file = new File(plugin.getDataFolder() + File.separator + "scrollInv" + File.separator + "scrollInv.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configInv = config.getConfigurationSection(YMLNavigate.INVENTORY);
        if (configInv == null) {
            System.err.println("Error getting any inventory from the yml..");
            return;
        }
        ConfigurationSection configInvAll = configInv.getConfigurationSection(YMLNavigate.INVENTORY_ALL);
        if (configInvAll == null) {
            System.err.println("Error getting the all inventory from the yml..");
            return;
        }

        int i = 1;
        ConfigurationSection configInvAllItem = configInvAll.getConfigurationSection(String.format("%s%d", YMLNavigate.ITEM, i++));

        // get all the items in inv all
        while (configInvAllItem != null) {
            ItemStack item = getItemFromConfig(configInvAllItem);
            configInvAllItem = configInvAll.getConfigurationSection(String.format("%s%d", YMLNavigate.ITEM, i++));
            scrollInvAll.addItem(item);
            scrollInvAllEdit.addItem(item);
        }
    }

    private static ItemStack getItemFromConfig(ConfigurationSection config) {
        //todo update these vvv
        String type = config.getString(YMLNavigate.MATERIAL);
        ItemStack item = new ItemStack(Material.getMaterial(type));
        List<String> lore = new ArrayList<String>(4);
        ConfigurationSection configLore = config.getConfigurationSection(YMLNavigate.LORE);
        // get the lore
        int i = 1;
        String loreLine = configLore.getString(String.format("%s%d", YMLNavigate.LINE, i++));
        while (loreLine != null) {
            lore.add(loreLine);
            loreLine = configLore.getString(String.format("%s%d", YMLNavigate.LINE, i++));
        }
        ItemMeta im = item.getItemMeta();
        im.setLore(lore);
        item.setItemMeta(im);
        item.setAmount(1);

        // 1 item in the stack
        return item;
    }

}