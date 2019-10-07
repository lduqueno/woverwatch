package fr.wondara.woverwatch.record;

import com.google.common.collect.Maps;
import com.mojang.authlib.properties.Property;
import fr.wondara.woverwatch.Timeline;
import fr.wondara.woverwatch.Woverwatch;
import fr.wondara.woverwatch.record.io.RecordSaver;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.ActionTransformer;
import fr.wondara.woverwatch.record.listener.packet.PacketTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import io.netty.channel.*;
import net.minecraft.server.v1_9_R1.Packet;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class Record {

    private Map<RecordingPlayer, Integer> recordInfos = Maps.newHashMap();
    private ConcurrentSkipListMap<Integer, LinkedHashSet<ActionContainer>> savedActions = new ConcurrentSkipListMap<>();
    private int startRecordTicks = 0;
    private boolean recording = true;
    private World world;

    public Record(List<RecordingPlayer> players) {
        startRecordTicks = Woverwatch.getInstance().getTimeline().getCurrentTick();
        int iteration = 0;
        for (RecordingPlayer player : players) {
            if (world == null)
                world = player.getPlayer().getWorld();
            else if(player.getPlayer().getWorld() != world){
                recordInfos.remove(player);
                break;
            }
            player.setStartMainHand(player.getPlayer().getInventory().getItemInMainHand());
            player.setStartOtherHand(player.getPlayer().getInventory().getItemInOffHand());
            player.setStartArmors(player.getPlayer().getInventory().getArmorContents());
            player.setStartLocation(player.getPlayer().getLocation());
            player.setStartPotionEffects(new ArrayList<>(player.getPlayer().getActivePotionEffects()));
            Property property = ((CraftPlayer) player.getPlayer()).getHandle().getProfile().getProperties().get("textures").iterator().next();
            player.setSkinValue(property.getValue());
            player.setSkinSignature(property.getSignature());
            player.setRecord(this);
            ChannelPipeline pipeline = ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.networkManager.channel.pipeline();
            pipeline.addBefore("packet_handler", player.getPlayer().getName(), new ChannelDuplexHandler() {
                public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                    super.channelRead(context, packet);
                }

                public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
                    if (packet instanceof Packet) {
                        Packet p = (Packet) packet;
                        ActionTransformer transformer = ActionTransformer.findTransformer(p.getClass().getSimpleName());
                        if (transformer != null)
                            ((PacketTransformer) transformer).handlePacket(p, player);
                    }
                    super.write(context, packet, channelPromise);
                }
            });
            recordInfos.put(player, iteration++);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!recording)
                    return;
                stopRecording(true);
            }
        }.runTaskLaterAsynchronously(Woverwatch.getInstance(), Timeline.TIMELINE_SAVE_TICKS);

    }

    public void addActionContainer(ActionContainer container) {
        int ticks = Woverwatch.getInstance().getTimeline().getCurrentTick() - startRecordTicks;
        LinkedHashSet<ActionContainer> list = savedActions.getOrDefault(ticks, new LinkedHashSet<>());
        list.add(container);
        savedActions.put(ticks, list);
        System.out.println("Registered " + container.getName() + " to current record!");
    }

    public int getActionsCount() {
        int total = 0;
        for (LinkedHashSet<ActionContainer> set : savedActions.values())
            total += set.size();
        return total;
    }

    public int getId(RecordingPlayer player){
        return recordInfos.get(player);
    }

    public void stopRecording(boolean save) {
        if (!recording)
            return;
        recording = false;
        if (save) {
            System.out.println("§6Enregistrement terminé ! (§7Packets enregistrés : " + getActionsCount() + "§76)");
            new RecordSaver(this).save();
        }
        for (RecordingPlayer player : new HashSet<>(recordInfos.keySet()))
            removePlayer(player);
        savedActions.values().forEach(HashSet::clear);
        savedActions.clear();
        startRecordTicks = 0;
    }

    public Map<RecordingPlayer, Integer> getRecordInfos() {
        return recordInfos;
    }

    public void removePlayer(RecordingPlayer player){
        recordInfos.remove(player);
        Channel channel = ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getPlayer().getName());
            return null;
        });
        player.clear();
        player.setRecord(null);
        if(recordInfos.isEmpty() && recording)
            stopRecording(false);
    }

    public ConcurrentSkipListMap<Integer, LinkedHashSet<ActionContainer>> getSavedActions() {
        return savedActions;
    }

    public int getStartRecordTicks() {
        return startRecordTicks;
    }

    public World getWorld() {
        return world;
    }
}
