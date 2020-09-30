package me.tererun.plugin.birthday.birthday;

import java.util.TimerTask;

public class BirthdayTask extends TimerTask {
    @Override
    public void run() {
        Birthday.setupBirthdayers();
    }
}
