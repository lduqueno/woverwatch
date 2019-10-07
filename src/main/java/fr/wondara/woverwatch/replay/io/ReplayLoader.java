package fr.wondara.woverwatch.replay.io;

import com.google.gson.*;
import fr.wondara.woverwatch.Woverwatch;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.replay.Replay;
import fr.wondara.woverwatch.replay.player.ReplayingPlayer;
import fr.wondara.woverwatch.util.ItemSerializer;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class ReplayLoader {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Replay.class, ((JsonDeserializer) (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonArray playersArray = object.get("players").getAsJsonArray();
        List<ReplayingPlayer> players = new ArrayList<>();
        World map = Bukkit.getWorld(object.get("map").getAsString());
        for (int i = 0; i < playersArray.size(); i++) {
            JsonObject playerObject = playersArray.get(i).getAsJsonObject();
            ItemStack[] armor = new ItemStack[4];
            int iteration = 0;
            for (String split : playerObject.get("armor").getAsString().split(":"))
                armor[iteration++] = ItemSerializer.deserializeItem(split);
            String[] locSplit = playerObject.get("location").getAsString().split(":");
            Location location = new Location(map, Double.parseDouble(locSplit[0]), Double.parseDouble(locSplit[1]), Double.parseDouble(locSplit[2]));
            List<PotionEffect> potionEffects = new ArrayList<>();
            for (String split : playerObject.get("potions").getAsString().split(":")) {
                if (split.isEmpty())
                    continue;
                String[] subSplit = split.split(";");
                potionEffects.add(new PotionEffect(PotionEffectType.getByName(subSplit[0]), Integer.parseInt(subSplit[1]), Integer.parseInt(subSplit[2])));
            }
            players.add(new ReplayingPlayer(playerObject.get("name").getAsString(), UUID.fromString(playerObject.get("uuid").getAsString()),
                    ItemSerializer.deserializeItem(playerObject.get("mainHand").getAsString()), ItemSerializer.deserializeItem(
                    playerObject.get("otherHand").getAsString()), armor, location, potionEffects, playerObject.get("skinValue").getAsString(),
                    playerObject.get("skinSignature").getAsString(), playerObject.get("entityId").getAsInt(), playerObject.get("id").getAsInt()));
        }
        ConcurrentSkipListMap<Integer, LinkedHashSet<ActionContainer>> savedActions = new ConcurrentSkipListMap<>();
        JsonArray ticksArray = object.get("ticks").getAsJsonArray();
        for (int j = 0; j < ticksArray.size(); j++) {
            JsonObject tickObject = ticksArray.get(j).getAsJsonObject();
            int tick = tickObject.get("tick").getAsInt();
            JsonArray eventsArray = tickObject.get("actions").getAsJsonArray();
            LinkedHashSet<ActionContainer> events = new LinkedHashSet<>();
            for (int k = 0; k < eventsArray.size(); k++)
                events.add(ActionContainer.fromJson(eventsArray.get(k).getAsJsonObject()));
            savedActions.put(tick, events);
        }
        return new Replay(object.get("replayId").getAsInt(), players, savedActions, object.get("length").getAsInt(), map.getName());
    }
    )).create();

    private File file;

    public ReplayLoader(int id) {
        this.file = new File(Woverwatch.getInstance().getDataFolder(), "replay_" + id + ".json");
    }

    public Replay load() {
        try {
            if (!this.file.exists()) {
                return null;
            }
            FileInputStream is = new FileInputStream(this.file);
            Replay replay = GSON.fromJson(IOUtils.toString(is), Replay.class);
            is.close();
            return replay;
        } catch (Exception e) {
            System.out.println("Unable to create file!");
            e.printStackTrace();
            return null;
        }
    }
}