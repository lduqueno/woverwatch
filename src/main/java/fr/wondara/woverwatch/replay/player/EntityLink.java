package fr.wondara.woverwatch.replay.player;

import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class EntityLink {

    private int recordEntityId;
    private Entity replayEntity;

    public EntityLink(int recordEntityId) {
        this.recordEntityId = recordEntityId;
    }

    public EntityLink(int recordEntityId, Entity replayEntity) {
        this(recordEntityId);
        this.replayEntity = replayEntity;
    }

    public int getEntityOldId() {
        return this.recordEntityId;
    }

    public Entity getReplayEntity() {
        return this.replayEntity;
    }

    public void setReplayEntity(Entity replayEntity) {
        this.replayEntity = replayEntity;
    }

    public void die(Player... forWho) {
        for (Player player : forWho)
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(replayEntity.getId()));
        replayEntity.die();
        replayEntity.world.entityList.remove(replayEntity);
    }

}
