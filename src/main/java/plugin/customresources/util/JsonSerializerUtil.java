package plugin.customresources.util;

import com.google.gson.*;
import plugin.customresources.enums.CustomResourcesMachineState;

import java.lang.reflect.Type;

public class JsonSerializerUtil implements JsonSerializer<CustomResourcesMachineState>, JsonDeserializer<CustomResourcesMachineState> {

    @Override
    public CustomResourcesMachineState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString().toLowerCase();
        switch (value) {
            case "active":
                return CustomResourcesMachineState.Active;
            case "upgrading":
                return CustomResourcesMachineState.Upgrading;
            case "broken":
                return CustomResourcesMachineState.Broken;
            default:
                throw new JsonParseException("Invalid state value: " + value);
        }
    }

    @Override
    public JsonElement serialize(CustomResourcesMachineState state, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(state.toString().toLowerCase());
    }
}
