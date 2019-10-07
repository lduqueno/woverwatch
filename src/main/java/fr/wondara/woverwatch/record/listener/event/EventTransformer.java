package fr.wondara.woverwatch.record.listener.event;

import fr.wondara.woverwatch.Woverwatch;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.ActionTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;

public abstract class EventTransformer<T extends Event> extends ActionTransformer<T> implements EventExecutor {

    private static final Listener EMPTY_LISTENER = new Listener(){};

    protected boolean shouldIgnoreCancelled = false;

    public EventTransformer(Class<T> eventClass){
        super(eventClass);
    }

    @Override
    protected void register() {
        Bukkit.getPluginManager().registerEvent(actionClass, EMPTY_LISTENER, EventPriority.MONITOR, this, Woverwatch.getInstance());
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if(!actionClass.isAssignableFrom(event.getClass()))
            return;
        if(event instanceof Cancellable && ((Cancellable) event).isCancelled() && !shouldIgnoreCancelled)
            return;
        T bukkitEvent = actionClass.cast(event);
        for(RecordingPlayer player : RecordingPlayer.getPlayers())
            if(player.isRecording() && shouldBeRecordedFor(bukkitEvent, player)) {
                ActionContainer recordData = record(bukkitEvent);
                if(isPerPlayer)
                    recordData.set("player", player.getRecord().getId(player));
                player.getRecord().addActionContainer(recordData);
                if(!isPerPlayer) //If its a global event (ex: entity spawn), add the event only one time
                    break;
            }
    }

    public boolean isShouldIgnoreCancelled() {
        return shouldIgnoreCancelled;
    }
}
