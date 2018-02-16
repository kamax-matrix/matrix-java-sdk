/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Maxime Dor
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client.regular;

import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.client._SyncData;
import io.kamax.matrix.event.EventKey;
import io.kamax.matrix.event._MatrixEvent;
import io.kamax.matrix.event._MatrixStateEvent;
import io.kamax.matrix.json.MatrixJsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SyncDataJson extends MatrixJsonObject implements _SyncData {

    public class MatrixEventJson extends MatrixJsonObject implements _MatrixEvent {

        public MatrixEventJson(JsonObject obj) {
            super(obj);
        }

        @Override
        public String getId() {
            return findString(EventKey.Id.get()).orElse(""); // FIXME refactor event structure
        }

        @Override
        public String getType() {
            return getString(EventKey.Type.get());
        }

        @Override
        public Instant getTime() {
            return Instant.ofEpochMilli(getLong(EventKey.Timestamp.get()));
        }

        @Override
        public _MatrixID getSender() {
            return MatrixID.from(getString(EventKey.Sender.get())).acceptable();
        }
    }

    public class MatrixStateEventJson extends MatrixEventJson implements _MatrixStateEvent {

        public MatrixStateEventJson(JsonObject obj) {
            super(obj);
        }

        @Override
        public String getStateKey() {
            return getString(EventKey.StateKey.get());
        }

    }

    public class StateJson extends MatrixJsonObject implements _SyncData.State {

        private List<_MatrixStateEvent> events = new ArrayList<>();

        public StateJson(JsonObject obj) {
            super(obj);

            findArray("events").ifPresent(array -> array.forEach(el -> {
                events.add(new MatrixStateEventJson(asObj(el)));
            }));
        }

        @Override
        public List<_MatrixStateEvent> getEvents() {
            return events;
        }
    }

    public class TimelineJson extends MatrixJsonObject implements _SyncData.Timeline {

        private List<_MatrixEvent> events = new ArrayList<>();

        public TimelineJson(JsonObject obj) {
            super(obj);

            findArray("events").ifPresent(array -> array.forEach(el -> {
                events.add(new MatrixEventJson(asObj(el)));
            }));
        }

        @Override
        public List<_MatrixEvent> getEvents() {
            return events;
        }

        @Override
        public boolean isLimited() {
            return findString("limited").map("true"::equals).orElse(false);
        }

        @Override
        public String getPreviousBatchToken() {
            return getString("prev_batch");
        }
    }

    public class InvitedRoomJson extends MatrixJsonObject implements _SyncData.InvitedRoom {

        private String id;
        private State state;

        public InvitedRoomJson(String id, JsonObject data) {
            super(data);
            this.id = id;
            this.state = new StateJson(findObj("state").orElseGet(JsonObject::new));
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public State getState() {
            return state;
        }
    }

    public class JoinedRoomJson extends MatrixJsonObject implements _SyncData.JoinedRoom {

        private String id;
        private State state;
        private Timeline timeline;

        public JoinedRoomJson(String id, JsonObject data) {
            super(data);
            this.id = id;
            this.state = new StateJson(findObj("state").orElseGet(JsonObject::new));
            this.timeline = new TimelineJson(findObj("timeline").orElseGet(JsonObject::new));
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public State getState() {
            return state;
        }

        @Override
        public Timeline getTimeline() {
            return timeline;
        }
    }

    public class LeftRoomJson extends MatrixJsonObject implements _SyncData.LeftRoom {

        private String id;
        private State state;
        private Timeline timeline;

        public LeftRoomJson(String id, JsonObject data) {
            super(data);
            this.id = id;
            this.state = new StateJson(findObj("state").orElseGet(JsonObject::new));
            this.timeline = new TimelineJson(findObj("timeline").orElseGet(JsonObject::new));
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public State getState() {
            return state;
        }

        @Override
        public Timeline getTimeline() {
            return timeline;
        }
    }

    public class RoomsJson extends MatrixJsonObject implements _SyncData.Rooms {

        private Set<InvitedRoom> invited = new HashSet<>();
        private Set<JoinedRoom> joined = new HashSet<>();
        private Set<LeftRoom> left = new HashSet<>();

        public RoomsJson(JsonObject obj) {
            super(obj);
            findObj("invite").ifPresent(o -> o.entrySet().forEach(entry -> {
                invited.add(new InvitedRoomJson(entry.getKey(), asObj(entry.getValue())));
            }));

            findObj("join").ifPresent(o -> o.entrySet().forEach(entry -> {
                joined.add(new JoinedRoomJson(entry.getKey(), asObj(entry.getValue())));
            }));

            findObj("leave").ifPresent(o -> o.entrySet().forEach(entry -> {
                left.add(new LeftRoomJson(entry.getKey(), asObj(entry.getValue())));
            }));
        }

        @Override
        public Set<InvitedRoom> getInvited() {
            return invited;
        }

        @Override
        public Set<JoinedRoom> getJoined() {
            return joined;
        }

        @Override
        public Set<LeftRoom> getLeft() {
            return left;
        }

    }

    private String nextBatch;
    private RoomsJson rooms;

    public SyncDataJson(JsonObject data) {
        super(data);
        nextBatch = getString("next_batch");
        rooms = new RoomsJson(findObj("rooms").orElseGet(JsonObject::new));
    }

    @Override
    public String nextBatchToken() {
        return nextBatch;
    }

    @Override
    public Rooms getRooms() {
        return rooms;
    }

}
