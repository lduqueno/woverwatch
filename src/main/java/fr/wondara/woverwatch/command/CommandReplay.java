package fr.wondara.woverwatch.command;

import fr.wondara.woverwatch.Timeline;
import fr.wondara.woverwatch.record.Record;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.Replay;
import fr.wondara.woverwatch.replay.io.ReplayLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandReplay implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        RecordingPlayer recordingPlayer = RecordingPlayer.getPlayer(player);
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "record":
                    if(recordingPlayer.isRecording()){
                        player.sendMessage("§cVous êtes déjà entrain d'enregistrer !");
                        return false;
                    }
                    List<RecordingPlayer> list = new ArrayList<>();
                    for(Player all : player.getWorld().getPlayers()) {
                        RecordingPlayer other = RecordingPlayer.getPlayer(all);
                        if(other == null || other.isRecording())
                            continue;
                        list.add(other);
                    }
                    new Record(list);
                    player.sendMessage("§6Enregistrement en cours.. (§7Durée : " + (Timeline.TIMELINE_SAVE_TICKS / 20) + " secondes§6)");
                    break;
                case "load":
                    Replay replay = new ReplayLoader(Integer.parseInt(args[1])).load();
                    player.sendMessage("§6Replay chargé ! Son identifiant est " + replay.getId() + ".");
                    replay.send(player);
                    break;
                case "maxtime":
                    Timeline.TIMELINE_SAVE_TICKS = Integer.parseInt(args[1]);
                    player.sendMessage("§6Temps d'enregistrement fixé à : §e" + Timeline.TIMELINE_SAVE_TICKS + " ticks (" + (
                            Timeline.TIMELINE_SAVE_TICKS / 20) + " secondes).");
            }
        }
        return false;
    }
}
