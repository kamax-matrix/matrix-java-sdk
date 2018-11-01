package io.kamax.matrix.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.event.EventKey;
import io.kamax.matrix.json.event.MatrixJsonDirectEvent;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixJsonDirectEventTest {

    private final _MatrixID userId = MatrixID.from("user", "example.org").valid();

    private MatrixJsonDirectEvent makeEvent(JsonObject content) {
        JsonObject obj = new JsonObject();
        obj.addProperty(EventKey.Type.get(), "m.direct");
        obj.add(EventKey.Content.get(), content);
        return new MatrixJsonDirectEvent(obj);
    }

    @Test
    public void contentEmpty() {
        MatrixJsonDirectEvent event = makeEvent(new JsonObject());
        assertTrue(event.getMappings().isEmpty());
    }

    @Test(expected = InvalidJsonException.class)
    public void valueNull() {
        makeEvent(GsonUtil.makeObj(userId.getId(), null));
    }

    @Test
    public void valueEmpty() {
        JsonObject content = GsonUtil.makeObj(userId.getId(), new JsonArray());
        MatrixJsonDirectEvent event = makeEvent(content);
        assertEquals(1, event.getMappings().size());
        assertTrue(event.getMappings().get(userId).isEmpty());
    }

    @Test
    public void singleValuePresent() {
        String roomId = "!roomId:example.org";
        JsonObject content = GsonUtil.makeObj(userId.getId(), GsonUtil.asArray(roomId));
        MatrixJsonDirectEvent event = makeEvent(content);
        assertEquals(1, event.getMappings().size());
        assertEquals(1, event.getMappings().get(userId).size());
        assertEquals(roomId, event.getMappings().get(userId).get(0));
    }

    @Test(expected = InvalidJsonException.class)
    public void nullObject() {
        new MatrixJsonDirectEvent(null);
    }

    @Test(expected = InvalidJsonException.class)
    public void emptyObject() {
        new MatrixJsonDirectEvent(new JsonObject());
    }

    @Test(expected = InvalidJsonException.class)
    public void invalidType() {
        JsonObject obj = new JsonObject();
        obj.addProperty(EventKey.Type.get(), "m");
        obj.add(EventKey.Content.get(), new JsonObject());
        new MatrixJsonDirectEvent(obj);
    }

}
