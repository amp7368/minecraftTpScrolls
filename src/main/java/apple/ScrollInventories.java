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
        System.out.println("updating");
        scrollInvAll = Bukkit.createInventory(new InventoryChest(54, "Scrolls"), 54, "PublicScrolls");
        scrollInvAllEdit = Bukkit.createInventory(new InventoryChest(54, "ScrollsEdit"), 54, "ScrollsEdit (changes the public scroll list)");
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

        int i = 0;
        ConfigurationSection configInvAllItem = configInvAll.getConfigurationSection(String.format("%s%d", YMLNavigate.ITEM, i));

        // get all the items in inv all
        while (configInvAllItem != null) {
            ItemStack item = getItemFromConfig(configInvAllItem);
            scrollInvAll.setItem(i, item);
            scrollInvAllEdit.setItem(i, new ItemStack(item));
            configInvAllItem = configInvAll.getConfigurationSection(String.format("%s%d", YMLNavigate.ITEM, ++i));
        }

    }

    private static ItemStack getItemFromConfig(ConfigurationSection config) {
        String type = config.getString(YMLNavigate.MATERIAL);
        if (type == null)
            return new ItemStack(Material.AIR);
        Material materialType = Material.getMaterial(type);
        if (materialType == null)
            return new ItemStack(Material.AIR);
        ItemStack item = new ItemStack(materialType);
        item.setAmount(1);

        // get the name of the item
        String name = config.getString(YMLNavigate.NAME);
        if (name !=null){
            ItemMeta im = item.getItemMeta();
            if(im != null){
                im.setDisplayName(name);
                item.setItemMeta(im);
            }
        }

        List<String> lore = new ArrayList<String>(4);
        ConfigurationSection configLore = config.getConfigurationSection(YMLNavigate.LORE);

        if (configLore == null) {
            return item;
        }

        // get the lore
        int i = 1;
        String loreLine = configLore.getString(String.format("%s%d", YMLNavigate.LINE, i++));
        while (loreLine != null) {
            lore.add(loreLine);
            loreLine = configLore.getString(String.format("%s%d", YMLNavigate.LINE, i++));
        }
        ItemMeta im = item.getItemMeta();

        // wtf happened if this is true
        if (im == null)
            return item;
        im.setLore(lore);
        item.setItemMeta(im);

        // 1 item in the stack
        return item;
    }

}
