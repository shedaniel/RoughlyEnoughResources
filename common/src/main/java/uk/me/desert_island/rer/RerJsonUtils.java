package uk.me.desert_island.rer;

import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import uk.me.desert_island.rer.mixin.IdentifierHooks;
import java.math.*;
import java.util.*;

public class RerJsonUtils {
    public static JsonElement optimiseTable(JsonElement element) {
        if (element.isJsonPrimitive()) {
            if (element.getAsJsonPrimitive().isString()) {
                String s = element.getAsJsonPrimitive().getAsString();
                if (s.length() >= 11 && s.startsWith("minecraft:")) {
                    String substring = s.substring(10);
                    if (IdentifierHooks.isPathValid(substring))
                        return new JsonPrimitive(substring);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                array.set(i, optimiseTable(array.get(i)));
            }
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            Set<String> keys = new HashSet<>();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                keys.add(entry.getKey());
            }
            for (String key : keys) {
                object.add(key, optimiseTable(object.get(key)));
            }
        }
        return element;
    }

    public static void writeIdentifier(FriendlyByteBuf buf, ResourceLocation identifier) {
        if (identifier.getNamespace().equals("minecraft")) {
            buf.writeUtf(identifier.getPath());
        } else {
            buf.writeUtf(identifier.toString());
        }
    }

    // NULL 0
    // STR 1
    // FALSE 2
    // TRUE 3
    // INT 4
    // LONG 5
    // SHORT 6
    // BYTE 7
    // BIG_INT 8
    // FLOAT 9
    // DOUBLE 10
    // BIG_DECIMAL 11
    // ARRAY 12
    // OBJECT 13
    public static void writeJson(FriendlyByteBuf buf, JsonElement element) {
        if (element.isJsonNull()) {
            buf.writeByte(0);
        } else if (element.isJsonPrimitive()) {
            writeJsonPrimitive(buf, element.getAsJsonPrimitive());
        } else if (element.isJsonArray()) {
            buf.writeByte(12);
            JsonArray array = element.getAsJsonArray();
            buf.writeVarInt(array.size());
            for (JsonElement arrayElement : array) {
                writeJson(buf, arrayElement);
            }
        } else if (element.isJsonObject()) {
            buf.writeByte(13);
            JsonObject object = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
            buf.writeVarInt(entrySet.size());
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                buf.writeUtf(entry.getKey());
                writeJson(buf, entry.getValue());
            }
        } else {
            throw new IllegalArgumentException("Unknown json element type: " + element.getClass());
        }
    }

    public static void writeJsonPrimitive(FriendlyByteBuf buf, JsonPrimitive primitive) {
        if (primitive.isString()) {
            buf.writeByte(1);
            buf.writeUtf(primitive.getAsString());
        } else if (primitive.isBoolean()) {
            buf.writeByte(primitive.getAsBoolean() ? 3 : 2);
        } else if (primitive.isNumber()) {
            Number number = primitive.getAsNumber();
            if (number instanceof Integer) {
                buf.writeByte(4);
                buf.writeVarInt(number.intValue());
            } else if (number instanceof Long) {
                buf.writeByte(5);
                buf.writeVarLong(number.longValue());
            } else if (number instanceof Short) {
                buf.writeByte(6);
                buf.writeShort(number.shortValue());
            } else if (number instanceof Byte) {
                buf.writeByte(7);
                buf.writeByte(number.byteValue());
            } else if (number instanceof BigInteger) {
                buf.writeByte(8);
                byte[] bytes = ((BigInteger) number).toByteArray();
                buf.writeByteArray(bytes);
            } else if (number instanceof Float) {
                buf.writeByte(9);
                buf.writeFloat(number.floatValue());
            } else if (number instanceof Double) {
                buf.writeByte(10);
                buf.writeDouble(number.doubleValue());
            } else if (number instanceof BigDecimal decimal) {
                buf.writeByte(11);
                // serialize with unscaled value, scale, and precision
                buf.writeByteArray(decimal.unscaledValue().toByteArray());
                buf.writeInt(decimal.scale());
                buf.writeInt(decimal.precision());
            } else {
                throw new IllegalArgumentException("Unknown number type: " + number.getClass());
            }
        } else {
            throw new IllegalArgumentException("Unknown primitive type: " + primitive.getClass());
        }
    }

    public static JsonElement readJson(FriendlyByteBuf buf) {
        byte type = buf.readByte();
        switch (type) {
            case 0:
                return JsonNull.INSTANCE;
            case 12:
                int size = buf.readVarInt();
                JsonArray array = new JsonArray(size);
                for (int i = 0; i < size; i++) {
                    array.add(readJson(buf));
                }
                return array;
            case 13:
                size = buf.readVarInt();
                JsonObject object = new JsonObject();
                for (int i = 0; i < size; i++) {
                    String key = buf.readUtf();
                    object.add(key, readJson(buf));
                }
                return object;
            default:
                if (type < 1 || type > 11) {
                    throw new IllegalArgumentException("Unknown json type: " + type);
                }
                return readJsonPrimitive(type, buf);
        }
    }

    public static JsonPrimitive readJsonPrimitive(int type, FriendlyByteBuf buf) {
        switch (type) {
            case 1:
                return new JsonPrimitive(buf.readUtf());
            case 2:
                return new JsonPrimitive(false);
            case 3:
                return new JsonPrimitive(true);
            case 4:
                return new JsonPrimitive(buf.readVarInt());
            case 5:
                return new JsonPrimitive(buf.readVarLong());
            case 6:
                return new JsonPrimitive(buf.readShort());
            case 7:
                return new JsonPrimitive(buf.readByte());
            case 8:
                return new JsonPrimitive(new BigInteger(buf.readByteArray()));
            case 9:
                return new JsonPrimitive(buf.readFloat());
            case 10:
                return new JsonPrimitive(buf.readDouble());
            case 11:
                // deserialize with unscaled value, scale, and precision
                byte[] unscaledValue = buf.readByteArray();
                int scale = buf.readInt();
                int precision = buf.readInt();
                MathContext context = new MathContext(precision);
                return new JsonPrimitive(new BigDecimal(new BigInteger(unscaledValue), scale, context));
            default:
                throw new IllegalArgumentException("Unknown json primitive type: " + type);
        }
    }
}
