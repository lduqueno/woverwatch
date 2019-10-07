package fr.wondara.woverwatch.record.listener.packet;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.ActionTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;

import java.lang.reflect.Field;

public abstract class PacketTransformer<T extends Packet> extends ActionTransformer<T> {

    public PacketTransformer(Class<T> packetClass){
        super(packetClass);
    }

    @Override
    protected void register() {
    }

    public void handlePacket(Packet packet, RecordingPlayer player){
        T casted = getActionClass().cast(packet);
        if(shouldBeRecordedFor(casted, player)) {
            ActionContainer recordData = record(casted);
            if(isPerPlayer)
                recordData.set("player", player.getRecord().getId(player));
            player.getRecord().addActionContainer(recordData);
        }
    }

    protected Object getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }

}
