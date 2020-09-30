package me.tererun.plugin.birthday.birthday.task;

import me.tererun.plugin.birthday.birthday.Birthday;

import java.util.TimerTask;

public class BirthdayTask extends TimerTask {
    @Override
    public void run() {
        Birthday.setupBirthdayers();
    }
}
