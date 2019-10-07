package fr.wondara.woverwatch.record.listener.event.list.action;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import net.minecraft.server.v1_9_R1.PacketPlayInEntityAction;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SneakEvent extends AbstractActionEvent<PlayerToggleSneakEvent> {

    public SneakEvent() {
        super(PlayerToggleSneakEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PlayerToggleSneakEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer();
    }

    @Override
    public ActionContainer record(PlayerToggleSneakEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("sneaking", event.isSneaking());
        return container;
    }

    @Override
    protected PacketPlayInEntityAction.EnumPlayerAction eventToAction(ActionContainer container) {
        return container.getAsBoolean("sneaking") ? PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING :
                PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING;
    }
}
