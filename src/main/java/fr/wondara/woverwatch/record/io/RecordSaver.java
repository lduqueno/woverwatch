package fr.wondara.woverwatch.record.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.wondara.woverwatch.Timeline;
import fr.wondara.woverwatch.Woverwatch;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashSet;
import java.util.Map;

import fr.wondara.woverwatch.record.Record;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.util.ItemSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class RecordSaver {

    private File file;
    private Record record;

    public RecordSaver(Record record) {
        this.record = record;
    }

    public void save() {
        try {
            long startTime = System.currentTimeMillis();
            File parent = Woverwatch.getInstance().getDataFolder();
            if (!parent.exists() && !parent.mkdirs())
                throw new Exception("parent mkdirs() return false");
            File[] list = parent.listFiles();
            int id = (list == null ? 1 : list.length + 1) - 1;
            file = new File(Woverwatch.getInstance().getDataFolder(), "replay_" + id + ".json");
            if (!file.createNewFile())
                throw new Exception("createNewFile() return false");
            FileWriter writer = new FileWriter(file);
            JsonObject replayObject = new JsonObject();
            replayObject.addProperty("replayId", id);
            replayObject.addProperty("map", record.getWorld().getName());
            replayObject.addProperty("length", Woverwatch.getInstance().getTimeline().getCurrentTick() - record.getStartRecordTicks());
            JsonArray playersArray = new JsonArray();
            for (RecordingPlayer player : record.getRecordInfos().keySet()) {
                JsonObject playerObject = new JsonObject();
                playerObject.addProperty("name", player.getName());
                playerObject.addProperty("entityId", player.getPlayer().getEntityId());
                playerObject.addProperty("uuid", player.getUUID().toString());
                playerObject.addProperty("id", record.getId(player));
                playerObject.addProperty("mainHand", ItemSerializer.serializeItem(player.getStartItemInMainHand()));
                playerObject.addProperty("otherHand", ItemSerializer.serializeItem(player.getStartItemInOtherHand()));
                StringBuilder armor = new StringBuilder();
                for (ItemStack item : player.getStartArmors())
                    armor.append(ItemSerializer.serializeItem(item)).append(":");
                String location = player.getStartLocation().getX() + ":" +
                        player.getStartLocation().getY() + ":" +
                        player.getStartLocation().getZ();
                playerObject.addProperty("armor", armor.toString().substring(0, armor.toString().length() - 1));
                playerObject.addProperty("location", location);
                StringBuilder potionEffects = new StringBuilder();
                for (PotionEffect potion : player.getStartPotionEffects())
                    potionEffects.append(potion.getType().getName()).append(";").append(potion.getDuration()).append(";").append(potion.getAmplifier())
                            .append(":");
                playerObject.addProperty("potions", potionEffects.toString().substring(0, Math.max(potionEffects.toString().length(), 1) - 1));
                playerObject.addProperty("skinValue", player.getSkinValue());
                playerObject.addProperty("skinSignature", player.getSkinSignature());
                playersArray.add(playerObject);
            }
            replayObject.add("players", playersArray);
            JsonArray ticksArray = new JsonArray();
            for (Map.Entry<Integer, LinkedHashSet<ActionContainer>> actions : record.getSavedActions().entrySet()) {
                JsonObject currentTickObject = new JsonObject();
                JsonArray actionsArray = new JsonArray();
                for (ActionContainer event : actions.getValue())
                    actionsArray.add(event.toJson());
                currentTickObject.add("tick", new JsonPrimitive(String.valueOf(actions.getKey())));
                currentTickObject.add("actions", actionsArray);
                ticksArray.add(currentTickObject);
            }
            replayObject.add("ticks", ticksArray);
            writer.write(replayObject.toString());
            writer.flush();
            writer.close();
            System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms to create file!");
        } catch (Exception e) {
            System.out.println("Unable to create file!");
            e.printStackTrace();
        }
    }
}