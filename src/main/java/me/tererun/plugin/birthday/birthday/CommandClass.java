package me.tererun.plugin.birthday.birthday;

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
                if (sender.isOp()) {
                    if (args.length == 1) {
                        BirthdaySetGUI.openInputGUI(player, args[0]);
                        return true;
                    } else {
                        BirthdaySetGUI.openInputGUI(player, player.getUniqueId().toString());
                        return true;
                    }
                } else {
                    if (Birthday.databaseDriver.getCount("birthday", player.getUniqueId().toString()) == 0) {
                        BirthdaySetGUI.openInputGUI(player, player.getUniqueId().toString());
                        return true;
                    } else {
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
