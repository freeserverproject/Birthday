package me.tererun.plugin.birthday.birthday;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class BirthdaySetGUI {
    public static void openInputGUI(Player myPlayer, String uuid) {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {
                    try {
                        Date sdfDate = sdf.parse(text);
                        String[] days = text.split("/");
                        String year = days[0];
                        String month = days[1];
                        String day = days[2];
                        if (month.length() == 1) {
                            month = "0" + month;
                        }
                        if (day.length() == 1) {
                            day = "0" + day;
                        }
                        String date = month + day;
                        Birthday.databaseDriver.addData("birthday", uuid, year, date);
                        Calendar birthCalender = Calendar.getInstance();
                        birthCalender.setTime(sdfDate);
                        if ((cl.get(Calendar.MONTH) == birthCalender.get(Calendar.MONTH)) && (cl.get(Calendar.DAY_OF_MONTH) == birthCalender.get(Calendar.DAY_OF_MONTH))) {
                            Birthday.birthdayers.add(player.getUniqueId());
                            Bukkit.broadcastMessage(Birthday.prefix + "§e本日は " + player.getName() + " さんの誕生日です！");
                        } else {
                            if (Birthday.birthdayers.contains(player.getUniqueId())) {
                                Birthday.birthdayers.remove(player.getUniqueId());
                            }
                        }
                        player.sendMessage(Birthday.prefix + "§a" + player.getName() + " さんの誕生日を§e " + text + " §aに設定しました");
                        player.sendMessage(Birthday.prefix + "§c変更する場合は運営に問い合わせをお願いします");
                        return AnvilGUI.Response.close();
                    } catch (ParseException e) {
                        Random random = new Random();
                        return AnvilGUI.Response.text("§4有効な形で入力してください§" + String.valueOf(random.nextInt(10)));
                    }
                })
                .text(sdf.format(cl.getTime()))
                .item(new ItemStack(Material.GOLD_BLOCK))
                .title("§dyyyy/MM/ddの形で入力してください")
                .plugin(Birthday.plugin)
                .open(myPlayer);
    }

}
