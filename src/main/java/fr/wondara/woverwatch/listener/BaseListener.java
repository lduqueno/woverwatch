package fr.wondara.woverwatch.listener;

import fr.wondara.woverwatch.record.player.RecordingPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BaseListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        RecordingPlayer player = RecordingPlayer.getPlayer(event.getPlayer());
        if(player.isRecording())
            player.getRecord().removePlayer(player);
        RecordingPlayer.getPlayers().remove(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        RecordingPlayer player = RecordingPlayer.getPlayer(event.getEntity());
        if(player.isRecording())
            player.getRecord().removePlayer(player);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event){
        RecordingPlayer player = RecordingPlayer.getPlayer(event.getPlayer());
        if(player.isRecording() && event.getPlayer().getWorld() != player.getRecord().getWorld())
            player.getRecord().removePlayer(player);
    }

}