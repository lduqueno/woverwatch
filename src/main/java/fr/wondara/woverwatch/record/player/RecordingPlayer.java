package fr.wondara.woverwatch.record.player;

import com.google.common.collect.Lists;
import fr.wondara.woverwatch.PlayerInfo;
import fr.wondara.woverwatch.Timeline;
import fr.wondara.woverwatch.Woverwatch;
import fr.wondara.woverwatch.record.Record;
import fr.wondara.woverwatch.record.io.RecordSaver;
import fr.wondara.woverwatch.record.listener.ActionTransformer;
import fr.wondara.woverwatch.record.listener.packet.PacketTransformer;
import io.netty.channel.*;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

public class RecordingPlayer extends PlayerInfo {

    private static List<RecordingPlayer> players = Lists.newArrayList();

    private Player player;
    private Record record;

    public RecordingPlayer(Player player) {
        super(player.getName(), player.getUniqueId(), null, null, null, null, null,
                null, null);
        this.player = player;
        players.add(this);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRecording() {
        return record != null;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record){
        this.record = record;
    }

    public static RecordingPlayer getPlayer(Player player) {
        Optional<RecordingPlayer> optional = players.stream().filter(p -> p.getName().equals(player.getName())).findFirst();
        return optional.orElseGet(() -> new RecordingPlayer(player));
    }

    public static List<RecordingPlayer> getPlayers() {
        return players;
    }

}
