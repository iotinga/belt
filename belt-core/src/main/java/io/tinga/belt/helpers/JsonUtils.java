package io.tinga.belt.helpers;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtils {

    public JsonNode subtract(JsonNode a, JsonNode b) throws UnsupportedOperationException {
        if (a == null || b == null)
            return NullNode.instance;

        if (a.getNodeType() == b.getNodeType()) {
            return switch (a.getNodeType()) {
                case JsonNodeType.BOOLEAN -> BooleanNode.valueOf(a.asBoolean() && b.asBoolean());
                case JsonNodeType.ARRAY -> throw new UnsupportedOperationException();
                case JsonNodeType.OBJECT -> diff((ObjectNode) a, (ObjectNode) b);
                case JsonNodeType.NUMBER,
                        JsonNodeType.STRING,
                        JsonNodeType.NULL,
                        JsonNodeType.BINARY,
                        JsonNodeType.POJO,
                        JsonNodeType.MISSING ->
                    b;
            };
        }
        return b;
    }

    @SuppressWarnings("deprecation")
    public ObjectNode diff(ObjectNode a, ObjectNode b) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

        Iterator<String> aKeys = a.fieldNames();
        while (aKeys.hasNext()) {
            objectNode.putNull(aKeys.next());
        }

        Iterator<String> bKeys = b.fieldNames();
        while (bKeys.hasNext()) {
            String key = bKeys.next();
            if (a.has(key)) {
                if (!deepEquals(a.get(key), b.get(key))) {
                    objectNode.put(key, b.get(key));
                } else {
                    objectNode.remove(key);
                }
            } else {
                objectNode.put(key, b.get(key));
            }
        }

        return objectNode;
    }

    public void deepMerge(JsonNode target, JsonNode... sources) {
        if (sources.length == 0 || target == null)
            return;

        for (JsonNode source : sources) {
            this.deepMerge(target, source);
        }
    }

    @SuppressWarnings("deprecation")
    public void deepMerge(JsonNode target, JsonNode source) {
        if (source == null || target == null)
            return;

        switch (target.getNodeType()) {
            case JsonNodeType.STRING:
            case JsonNodeType.NUMBER:
            case JsonNodeType.NULL:
            case JsonNodeType.BINARY:
            case JsonNodeType.MISSING:
            case JsonNodeType.POJO:
            case JsonNodeType.BOOLEAN:
                break;
            case JsonNodeType.ARRAY:
                if (source.isArray()) {
                    ((ArrayNode) target).addAll((ArrayNode) source);
                } else {
                    ((ArrayNode) target).add(source);
                }
                break;
            case JsonNodeType.OBJECT:
                if (source.isObject()) {
                    Iterator<String> sourceKeys = source.fieldNames();
                    while (sourceKeys.hasNext()) {
                        String key = sourceKeys.next();
                        if (target.has(key)) {
                            deepMerge(target.get(key), source.get(key));
                        } else {
                            ((ObjectNode) target).put(key, source.get(key));
                        }
                    }
                }
                break;
        }
    }

    public boolean deepEquals(JsonNode a, JsonNode b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getNodeType() != b.getNodeType()) {
            return false;
        }

        return switch (a.getNodeType()) {
            case JsonNodeType.NUMBER,
                    JsonNodeType.NULL,
                    JsonNodeType.BINARY,
                    JsonNodeType.MISSING,
                    JsonNodeType.POJO,
                    JsonNodeType.BOOLEAN,
                    JsonNodeType.STRING ->
                a.equals(b);
            case JsonNodeType.ARRAY -> {
                ArrayNode arrayA = (ArrayNode) a;
                ArrayNode arrayB = (ArrayNode) b;
                if (arrayA.size() != arrayB.size()) {
                    yield false;
                }
                for (int i = 0; i < arrayA.size(); i++) {
                    if (!deepEquals(arrayA.get(i), arrayB.get(i))) {
                        yield false;
                    }
                }
                yield true;
            }
            case JsonNodeType.OBJECT -> {
                ObjectNode objA = (ObjectNode) a;
                ObjectNode objB = (ObjectNode) b;
                if (objA.size() != objB.size()) {
                    yield false;
                }
                Iterator<String> aKeys = objA.fieldNames();
                while (aKeys.hasNext()) {
                    String key = aKeys.next();
                    if (!deepEquals(objA.get(key), objB.get(key))) {
                        yield false;
                    }
                }
                yield true;
            }
        };

    }
}