package me.tererun.plugin.birthday.birthday.commands;

import me.tererun.plugin.birthday.birthday.Birthday;
import me.tererun.plugin.birthday.birthday.gui.BirthdaySetGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClass implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("birthday")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                if (sender.isOp()) {
                    if (args.length == 1) {
                        BirthdaySetGUI.openInputGUI(player, Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString());
                        return true;
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("delete")) {
                            Birthday.databaseDriver.deleteData("birthday", Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                            return true;
                        } else {
                            BirthdaySetGUI.openInputGUI(player, uuid);
                            return true;
                        }
                    } else {
                        BirthdaySetGUI.openInputGUI(player, uuid);
                        return true;
                    }
                } else {
                    if (Birthday.databaseDriver.getCount("birthday", player.getUniqueId().toString()) == 0) {
                        BirthdaySetGUI.openInputGUI(player, uuid);
                        return true;
                    } else {
                        player.sendMessage(Birthday.prefix + "§c既に誕生日を設定済みです§f: " + Birthday.dateToString(Birthday.getDateFromUUID(uuid)));
                        player.sendMessage(Birthday.prefix + "§c変更する場合は運営に問い合わせをお願いします");
                        return true;
                    }
                }
            } else {
                sender.sendMessage(Birthday.prefix + "このコマンドはプレイヤーからのみ実行可能です");
                return true;
            }
        }
        return false;
    }
}
