package me.tererun.plugin.birthday.birthday.events;

import org.bukkit.entity.Player;

import java.util.Date;

public class BirthdaySetEvent extends BirthdayEvent {
    public BirthdaySetEvent(Player player, Date date) {
        super(player, date);
    }
}
