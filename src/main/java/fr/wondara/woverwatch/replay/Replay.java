package fr.wondara.woverwatch.replay;

import com.google.common.collect.Lists;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.replay.player.ReplayingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

public class Replay {

    private int id;
    private List<ReplayingPlayer> players = Lists.newArrayList();
    private ConcurrentSkipListMap<Integer, LinkedHashSet<ActionContainer>> savedActions;
    private int length;
    private World map;

    public Replay(int id, List<ReplayingPlayer> players, ConcurrentSkipListMap<Integer, LinkedHashSet<ActionContainer>> savedActions, int length,
                  String map) {
        this.id = id;
        this.players = players;
        this.savedActions = savedActions;
        this.length = length;
        this.map = Bukkit.getWorld(map);
    }

    public void send(Player moderator) {
        new ReplayTask(this, moderator);
    }

    public int getId() {
        return id;
    }

    public ConcurrentSkipListMap<Integer, LinkedHashSet<ActionContainer>> getSavedActions() {
        return savedActions;
    }

    public int getLength() {
        return length;
    }

    public World getMap() {
        return map;
    }

    public List<ReplayingPlayer> getPlayers() {
        return players;
    }
}
