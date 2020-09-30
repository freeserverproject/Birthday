package me.tererun.plugin.birthday.birthday.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;

public class BirthdayEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Date date;

    public BirthdayEvent(Player player, Date date) {
        this.player = player;
        this.date = date;
    }

    public Player getPlayer() {
        return player;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
