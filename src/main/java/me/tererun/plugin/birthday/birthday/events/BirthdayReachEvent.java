package me.tererun.plugin.birthday.birthday.events;

import org.bukkit.entity.Player;

import java.util.Date;

public class BirthdayReachEvent extends BirthdayEvent {
    public BirthdayReachEvent(Player player, Date date) {
        super(player, date);
    }
}
