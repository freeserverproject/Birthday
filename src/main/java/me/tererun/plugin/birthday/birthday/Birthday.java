package me.tererun.plugin.birthday.birthday;

import dev.dbassett.skullcreator.SkullCreator;
import me.tererun.plugin.birthday.birthday.commands.CommandClass;
import me.tererun.plugin.birthday.birthday.database.DatabaseDriver;
import me.tererun.plugin.birthday.birthday.listeners.EventClass;
import me.tererun.plugin.birthday.birthday.serialize.BukkitSerialization;
import me.tererun.plugin.birthday.birthday.task.BirthdayTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Birthday extends JavaPlugin {

    public static Plugin plugin;
    public static NamespacedKey key;
    public static DatabaseDriver databaseDriver;
    public static FileConfiguration config;
    public static String prefix = "§f[§cBirthday§f\uD83C\uDF82]: ";
    public static HashSet<UUID> birthdayers = new HashSet<>();

    @Override
    public void onEnable() {
        registerPlugin();
        registerConfig();
        registerRecipe();
        registerCommands();
        registerEvents();
        registerDatabase();
        registerScheduler();
        setupBirthdayers();
    }

    private void registerCommands() {
        getCommand("birthday").setExecutor(new CommandClass());
    }

    public static void setupBirthdayers() {
        databaseDriver.loadAllData("birthday", null);
    }

    private void registerScheduler() {
        Timer timer = new Timer();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        timer.schedule(new BirthdayTask(), calendar.getTime());
    }

    private void registerConfig() {
        saveDefaultConfig();
        config = getConfig();
    }

    private void registerPlugin() {
        plugin = this;
        key = new NamespacedKey(plugin, "Birthday");
    }

    private void registerDatabase() {
        databaseDriver = new DatabaseDriver("data.db", "birthday");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new EventClass(), this);
    }

    public void registerRecipe() {
        NamespacedKey pBKey = new NamespacedKey(plugin, "present_box");
        ShapedRecipe pBrecipe = new ShapedRecipe(pBKey, getPresentBox().clone());
        pBrecipe.shape("PPP", "PRP", "PPP");
        pBrecipe.setIngredient('P', new RecipeChoice.MaterialChoice(Material.ACACIA_PLANKS, Material.BIRCH_PLANKS, Material.CRIMSON_PLANKS, Material.DARK_OAK_PLANKS, Material.OAK_PLANKS, Material.JUNGLE_PLANKS, Material.SPRUCE_PLANKS, Material.WARPED_PLANKS));
        pBrecipe.setIngredient('R', Material.RED_DYE);
        if (Bukkit.getRecipesFor(getPresentBox().clone()).size() == 0) {
            Bukkit.addRecipe(pBrecipe);
        }
    }

    private ItemStack getPresentBox() {
        ItemStack presentBox = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=");
        ItemMeta presentMeta = presentBox.getItemMeta();
        presentMeta.setDisplayName("§e§oプレゼントボックス");
        presentMeta.setLore(Arrays.asList("§aプレゼントが入れられる§fプレゼントボックス", "§6§l右クリックで開ける"));
        presentMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, BukkitSerialization.toBase64(Bukkit.createInventory(null, 9, "§e§oプレゼントボックス")));
        presentBox.setItemMeta(presentMeta);
        return presentBox;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        return sdf.format(date);
    }

    public static Date getDateFromUUID(String uuid) {
        List<String> result = Birthday.databaseDriver.loadData("birthday", uuid);
        String year = result.get(1);
        String day = result.get(2);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        try {
            return sdf.parse(year + day);
        } catch (ParseException parseException) {}
        return null;
    }
}
