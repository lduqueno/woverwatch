package fr.wondara.woverwatch.record.listener.event.list.action;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import net.minecraft.server.v1_9_R1.PacketPlayInEntityAction;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class SprintEvent extends AbstractActionEvent<PlayerToggleSprintEvent> {

    public SprintEvent() {
        super(PlayerToggleSprintEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PlayerToggleSprintEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer();
    }

    @Override
    public ActionContainer record(PlayerToggleSprintEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("sprinting", event.isSprinting());
        return container;
    }

    @Override
    protected PacketPlayInEntityAction.EnumPlayerAction eventToAction(ActionContainer container) {
        return container.getAsBoolean("sprinting") ? PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING :
                PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING;
    }
}
