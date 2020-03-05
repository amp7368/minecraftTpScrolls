package apple.listeners;

import apple.ScrollInventories;
import apple.utils.MessageFinals;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class InventoryInteractListener implements Listener {
    public InventoryInteractListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void inventoryEvent(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null)
            return;
        // if this isn't my inventory, I dont care
        if (holder.equals(ScrollInventories.scrollInvAll)) {
            dealWithAllInv(event);
        }
    }

    public void dealWithAllInv(InventoryClickEvent event) {
        HumanEntity who = event.getWhoClicked();
        // if this wasn't interacted with a player, wtf happened? best to ignore it..
        if (!(who instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) who;

        // don't treat this as a normal inv
        event.setCancelled(true);

        // Don't let the player use numbers to get stuff.
        if (event.getClick().equals(ClickType.NUMBER_KEY)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return;

        // get info about the clicked item
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        if (clickedMeta == null) {
            return;
        }
        List<String> clickedLore = clickedMeta.getLore();
        Material clickedType = clickedItem.getType();

        // get the player inv and contents to see if it's empty
        PlayerInventory playerInv = player.getInventory();
        ItemStack[] contents = playerInv.getStorageContents();
        boolean canAddItem = false;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getAmount() == 0 || item.getType().equals(Material.AIR)) {
                canAddItem = true;
                break;
            } else {
                // get info about item
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null)
                    continue;
                List<String> itemLore = itemMeta.getLore();

                // if we already have the item in our inv
                if (item.getType() == clickedType && itemLore != null && itemLore.equals(clickedLore)) {
                    if (item.getAmount() != 64) {
                        item.setAmount(64);
                        playerInv.setItem(i, item);
                        return;
                    }
                    player.sendMessage(MessageFinals.GET_ALREADY_HAVE);
                    break;
                }
            }
        }

        // if the player inv has an empty slot and we don't already have it add the item
        if (canAddItem) {
            if (event.getAction().compareTo(InventoryAction.PICKUP_HALF) == 0) {
                clickedItem.setAmount(1);
            } else {
                clickedItem.setAmount(64);
            }
            player.getInventory().addItem(clickedItem);
        }
    }
}