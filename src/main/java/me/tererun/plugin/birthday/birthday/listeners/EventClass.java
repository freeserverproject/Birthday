package me.tererun.plugin.birthday.birthday.listeners;

import me.tererun.plugin.birthday.birthday.Birthday;
import me.tererun.plugin.birthday.birthday.serialize.BukkitSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventClass implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (Birthday.birthdayers.contains(player.getUniqueId())) {
            Location location = player.getLocation();
            World world = player.getWorld();
            world.spawnParticle(Particle.END_ROD, location, 8, 1, 1, 1, 0);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Calendar calendar = Calendar.getInstance();
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (Birthday.databaseDriver.getCount("birthday", uuid) != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setLenient(false);
            Date birthDate = Birthday.getDateFromUUID(uuid);
            Calendar birthCalender = Calendar.getInstance();
            birthCalender.setTime(birthDate);
            if ((calendar.get(Calendar.MONTH) == birthCalender.get(Calendar.MONTH)) && (calendar.get(Calendar.DAY_OF_MONTH) == birthCalender.get(Calendar.DAY_OF_MONTH))) {
                Bukkit.broadcastMessage(Birthday.prefix + "§6本日の主役が登場！ §a" + player.getName() + " §eさんの誕生日です！");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) throws IOException {
        if ((e.getHand() == null) || (e.getItem() == null) || (!e.getItem().hasItemMeta())) return;
        if (e.getHand().equals(EquipmentSlot.HAND) && (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            ItemStack handItem = e.getItem();
            ItemMeta  handMeta = handItem.getItemMeta();
            PersistentDataContainer handContainer = handMeta.getPersistentDataContainer();
            if (handContainer.has(Birthday.key, PersistentDataType.STRING)) {
                if (handItem.getAmount() != 1) {
                    e.getPlayer().sendMessage("§7> §4プレゼントボックスはスタックした状態で開くことは出来ません!");
                    return;
                }
                String presentBoxInventoryByString = handContainer.get(Birthday.key, PersistentDataType.STRING);
                Inventory inventory = BukkitSerialization.fromBase64(presentBoxInventoryByString);
                e.getPlayer().openInventory(inventory);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if ((e.getItemInHand().hasItemMeta()) && (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(Birthday.key, PersistentDataType.STRING))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("§e§oプレゼントボックス")) {
            if (e.getClickedInventory() == null) e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(Birthday.key, PersistentDataType.STRING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (e.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("§e§oプレゼントボックス")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("§e§oプレゼントボックス")) {
            if ((e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) && (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Birthday.key, PersistentDataType.STRING))) {
                ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.getPersistentDataContainer().set(Birthday.key, PersistentDataType.STRING, BukkitSerialization.toBase64(e.getInventory()));
                itemStack.setItemMeta(itemMeta);
                e.getPlayer().getInventory().setItemInMainHand(itemStack);
            }
        }
    }

    /*
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getName().equalsIgnoreCase("tererun")) {
            Item item = e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), new ItemStack(Material.GOLD_BLOCK, 1));
            item.setPickupDelay(Integer.MAX_VALUE);
            e.getPlayer().addPassenger(item);
        }
    }

    @EventHandler
    public void onEntityItemPickup(EntityPickupItemEvent e) {
        if (e.getItem().getItemStack().getType().equals(Material.GOLD_BLOCK)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent e) {
        if (e.getEntity().getItemStack().getType().equals(Material.GOLD_BLOCK)) {
            e.setCancelled(true);
        }
    }
    */

}
