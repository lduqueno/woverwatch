package fr.wondara.woverwatch.record.listener.event.list.block;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;
import net.minecraft.server.v1_9_R1.PacketPlayOutBlockChange;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R1.util.CraftMagicNumbers;

import java.util.Collections;
import java.util.List;

public class BlockBreakEvent extends EventTransformer<org.bukkit.event.block.BlockBreakEvent> {

    public BlockBreakEvent() {
        super(org.bukkit.event.block.BlockBreakEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(org.bukkit.event.block.BlockBreakEvent event, RecordingPlayer player) {
        return isInRange(event.getBlock().getLocation(), event.getPlayer().getLocation());
    }

    @Override
    public ActionContainer record(org.bukkit.event.block.BlockBreakEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        Block block = event.getBlock();
        container.set("x", block.getLocation().getBlockX());
        container.set("y", block.getLocation().getBlockY());
        container.set("z", block.getLocation().getBlockZ());
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        double x = container.getAsDouble("x");
        double y = container.getAsDouble("y");
        double z = container.getAsDouble("z");
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(npc.getEntity().world, new BlockPosition(x, y, z));
        packet.block = CraftMagicNumbers.getBlock(0).fromLegacyData(0);
        return Collections.singletonList(packet);
    }

}
