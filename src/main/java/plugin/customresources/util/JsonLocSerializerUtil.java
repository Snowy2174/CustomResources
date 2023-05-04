package plugin.customresources.util;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import plugin.customresources.enums.CustomResourcesMachineState;

import java.lang.reflect.Type;
import java.util.UUID;

public class JsonLocSerializerUtil implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    String [] parts = jsonObject.getAsString().split(";"); //If you changed the semicolon you must change it here too
    int x = Integer.parseInt(parts[0]);
    int y = Integer.parseInt(parts[1]);
    int z = Integer.parseInt(parts[2]);
    UUID u = UUID.fromString(parts[3]);
    World world = Bukkit.getWorld(u);
    return new Location(world, x, y, z);
}

    @Override
    public JsonElement serialize(Location loc, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonLoc = new JsonObject();
        jsonLoc.addProperty("loc",loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getUID().toString());
        return jsonLoc;
    }
}
