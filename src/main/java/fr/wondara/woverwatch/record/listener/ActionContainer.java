package fr.wondara.woverwatch.record.listener;

import com.google.common.collect.Maps;
import com.google.gson.*;

import java.util.Map;

public class ActionContainer {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ActionContainer.class, (JsonDeserializer<ActionContainer>) (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject object = jsonElement.getAsJsonObject();
        ActionContainer var = new ActionContainer(object.get("type").getAsString());
        object = object.get("values").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            ActionContainer subContainer = fromJson(entry.getValue());
            Object value = entry.getValue().toString();
            if (subContainer != null)
                value = subContainer;
            var.set(entry.getKey(), value);
        }
        return var;
    }).create();

    private String name;
    private Map<String, Object> vars = Maps.newHashMap();

    public ActionContainer(Class<?> clazz) {
        this.name = clazz.getSimpleName();
    }

    private ActionContainer(String clazzName) {
        this.name = clazzName;
    }

    public boolean isEmpty() {
        return vars.isEmpty();
    }

    public boolean isPresent(String field) { return vars.containsKey(field); }

    public String getName() {
        return this.name;
    }

    public void set(String field, Object value) {
        this.vars.put(field, value);
    }

    public String get(String field) {
        return this.vars.get(field).toString().replaceAll("\"", "");
    }

    public Object getAsObject(String field) {
        return this.vars.get(field);
    }

    public ActionContainer getAsContainer(String field) {
        return (ActionContainer) this.getAsObject(field);
    }

    public int getAsInt(String field) {
        return Integer.parseInt(this.get(field));
    }

    public boolean getAsBoolean(String field) {
        return Boolean.parseBoolean(this.get(field));
    }

    public long getAsLong(String field) {
        return Long.parseLong(this.get(field));
    }

    public byte getAsByte(String field) {
        return Byte.parseByte(this.get(field));
    }

    public short getAsShort(String field) {
        return Short.parseShort(this.get(field));
    }

    public float getAsFloat(String field) {
        return Float.parseFloat(this.get(field));
    }

    public double getAsDouble(String field) {
        return Double.parseDouble(this.get(field));
    }

    public boolean is(String field, Object value) {
        return this.get(field) == null && value == null || this.get(field) != null && this.get(field).equals(value);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.name);
        JsonObject val = new JsonObject();
        for (Map.Entry<String, Object> entry : this.vars.entrySet())
            if (entry.getValue() instanceof ActionContainer)
                val.add(entry.getKey(), ((ActionContainer) entry.getValue()).toJson());
            else
                val.addProperty(entry.getKey(), entry.getValue().toString());
        json.add("values", val);
        return json;
    }

    public static ActionContainer fromJson(JsonElement json) {
        if (json.isJsonObject() && json.getAsJsonObject().has("type") && json.getAsJsonObject().has("values"))
            return GSON.fromJson(json, ActionContainer.class);
        return null;
    }
}
