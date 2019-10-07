package fr.wondara.woverwatch;

import org.bukkit.scheduler.BukkitRunnable;

public class Timeline extends BukkitRunnable {

    public static int TIMELINE_SAVE_TICKS = 600;
    private int currentTick = 0;

    public Timeline() {
        runTaskTimer(Woverwatch.getInstance(), 0L, 1L);
    }

    @Override
    public void run(){
        this.currentTick += 1;
    }

    public int getCurrentTick(){
        return this.currentTick;
    }
}
