package plugin.customresources.util;

import com.google.gson.*;
import plugin.customresources.objects.Machine;

import java.lang.reflect.Type;

public class JsonSerializerUtil implements JsonSerializer<Machine.CustomResourcesMachineState>, JsonDeserializer<Machine.CustomResourcesMachineState> {

    @Override
    public Machine.CustomResourcesMachineState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString().toLowerCase();
        switch (value) {
            case "active":
                return Machine.CustomResourcesMachineState.Active;
            case "upgrading":
                return Machine.CustomResourcesMachineState.Upgrading;
            case "broken":
                return Machine.CustomResourcesMachineState.Broken;
            default:
                throw new JsonParseException("Invalid state value: " + value);
        }
    }

    @Override
    public JsonElement serialize(Machine.CustomResourcesMachineState state, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(state.toString().toLowerCase());
    }
}
