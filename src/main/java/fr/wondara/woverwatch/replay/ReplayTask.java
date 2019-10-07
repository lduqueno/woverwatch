package fr.wondara.woverwatch.replay;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fr.wondara.woverwatch.Timeline;
import fr.wondara.woverwatch.Woverwatch;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.ActionTransformer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.replay.npc.NPC;
import fr.wondara.woverwatch.replay.player.EntityLink;
import fr.wondara.woverwatch.replay.player.ReplayingPlayer;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ReplayTask extends BukkitRunnable {

    private Replay replay;
    private Player viewer;
    private EntityPlayer craftViewer;
    private Map<ReplayingPlayer, NPC> players = Maps.newHashMap();
    private List<EntityLink> spawnedEntities = Lists.newArrayList();
    private int elapsedTicks = 0;

    public ReplayTask(Replay replay, Player viewer) {
        this.replay = replay;
        this.viewer = viewer;
        this.craftViewer = ((CraftPlayer) viewer).getHandle();
        viewer.setGameMode(GameMode.SPECTATOR);
        for (ReplayingPlayer info : replay.getPlayers()) {
            NPC npc = new NPC(info, info.getStartLocation());
            npc.spawnFor(viewer);
            info.getLink().setReplayEntity(npc.getEntity());
            players.put(info, npc);
            viewer.teleport(info.getStartLocation());
        }
        runTaskTimerAsynchronously(Woverwatch.getInstance(), 0, 1);
    }

    public void run() {
        if (viewer == null || !viewer.isOnline()) {
            stop();
            return;
        }
        HashSet<ActionContainer> clonedPackets = Sets.newHashSet();
        if (elapsedTicks++ > replay.getLength()) {
            stop();
            return;
        }
        LinkedHashSet<ActionContainer> containers = replay.getSavedActions().get(elapsedTicks - 1);
        if (containers == null || containers.isEmpty())
            return;
        for (ActionContainer container : containers) {
            ActionTransformer<?> transformer = ActionTransformer.findTransformer(container.getName());
            if (transformer == null || clonedPackets.contains(container))
                continue;
            clonedPackets.add(container);
            Runnable runnable = () -> {
                NPC player = null;
                if (transformer.isPerPlayer() && container.isPresent("player")) {
                    int id = container.getAsInt("player");
                    for (Map.Entry<ReplayingPlayer, NPC> entry : players.entrySet())
                        if (entry.getKey().getId() == id) {
                            player = entry.getValue();
                            break;
                        }
                }
                List<Packet<PacketListenerPlayOut>> packets = transformer.transformIntoPackets(container, this, player);
                packets.forEach(packet -> craftViewer.playerConnection.sendPacket(packet));
            };
            if (transformer.isShouldRunSync())
                Bukkit.getScheduler().runTask(Woverwatch.getInstance(), runnable);
            else
                runnable.run();
        }
        clonedPackets.clear();
    }

    public void stop() {
        elapsedTicks = 0;
        cancel();
        Bukkit.getScheduler().runTask(Woverwatch.getInstance(), () -> {
            viewer.setGameMode(GameMode.SURVIVAL);
            players.keySet().forEach(player -> player.getLink().die(viewer));
            players.clear();
            spawnedEntities.forEach(entity -> entity.die(viewer));
            spawnedEntities.clear();
        });
    }

    public EntityLink getEntityLinkFromOldId(int oldId) {
        ReplayingPlayer player = players.keySet().stream().filter(p -> p.getLink().getEntityOldId() == oldId).findFirst().orElse(null);
        if (player != null)
            return player.getLink();
        return spawnedEntities.stream().filter(entity -> entity.getEntityOldId() == oldId).findFirst().orElse(null);
    }

    public EntityLink getEntityLinkFromActualId(int actualId) {
        return spawnedEntities.stream().filter(entity -> entity.getReplayEntity().getId() == actualId).findFirst().orElse(null);
    }

    public Replay getReplay() {
        return replay;
    }

    public Player getViewer() {
        return viewer;
    }

    public Map<ReplayingPlayer, NPC> getPlayers() {
        return players;
    }

    public List<EntityLink> getEntitiesToRemove() {
        return spawnedEntities;
    }

    public int getElapsedTicks() {
        return elapsedTicks;
    }

}