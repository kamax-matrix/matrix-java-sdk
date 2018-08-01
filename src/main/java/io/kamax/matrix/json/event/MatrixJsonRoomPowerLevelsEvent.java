/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Arne Augenstein
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.json.event;

import com.google.gson.JsonObject;

import io.kamax.matrix.event._RoomPowerLevelsEvent;
import io.kamax.matrix.json.MatrixJsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MatrixJsonRoomPowerLevelsEvent extends MatrixJsonRoomEvent implements _RoomPowerLevelsEvent {

    private Content content;

    public MatrixJsonRoomPowerLevelsEvent(JsonObject obj) {
        super(obj);

        content = new Content(getObj("content"));
    }

    @Override
    public double getBan() {
        return content.getBan();
    }

    @Override
    public Map<String, Double> getEvents() {
        return content.getEvents();
    }

    @Override
    public double getEventsDefault() {
        return content.getEventsDefault();
    }

    @Override
    public double getInvite() {
        return content.getInvite();
    }

    @Override
    public double getKick() {
        return content.getKick();
    }

    @Override
    public double getRedact() {
        return content.getRedact();
    }

    @Override
    public double getStateDefault() {
        return content.getStateDefault();
    }

    @Override
    public Map<String, Double> getUsers() {
        return content.getUsers();
    }

    @Override
    public double getUsersDefault() {
        return content.getUsersDefault();
    }

    private class Content extends MatrixJsonObject {

        private double ban;
        private Map<String, Double> events = new HashMap<>();
        private double eventsDefault;
        private double invite;
        private double kick;
        private double redact;
        private double stateDefault;
        private Map<String, Double> users = new HashMap<>();
        private double usersDefault;

        Content(JsonObject obj) {
            super(obj);

            setBan(getDouble("ban"));
            setEventsDefault(getDouble("events_default"));
            setInvite(getDouble("invite"));
            setKick(getDouble("kick"));
            setRedact(getDouble("redact"));
            setStateDefault(getDouble("state_default"));
            setUsersDefault(getDouble("users_default"));

            JsonObject eventsJson = obj.getAsJsonObject("events");
            JsonObject usersJson = obj.getAsJsonObject("users");

            Map<String, Double> eventsMap = eventsJson.entrySet().stream()
                    .collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().getAsDouble()));

            Map<String, Double> usersMap = usersJson.entrySet().stream()
                    .collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().getAsDouble()));

            setEvents(eventsMap);
            setUsers(usersMap);
        }

        double getBan() {
            return ban;
        }

        void setBan(double ban) {
            this.ban = ban;
        }

        double getEventsDefault() {
            return eventsDefault;
        }

        void setEventsDefault(double eventsDefault) {
            this.eventsDefault = eventsDefault;
        }

        Map<String, Double> getEvents() {
            return events;
        }

        void setEvents(Map<String, Double> events) {
            this.events.putAll(events);
        }

        double getInvite() {
            return invite;
        }

        void setInvite(double invite) {
            this.invite = invite;
        }

        double getKick() {
            return kick;
        }

        void setKick(double kick) {
            this.kick = kick;
        }

        double getRedact() {
            return redact;
        }

        void setRedact(double redact) {
            this.redact = redact;
        }

        double getStateDefault() {
            return stateDefault;
        }

        void setStateDefault(double stateDefault) {
            this.stateDefault = stateDefault;
        }

        Map<String, Double> getUsers() {
            return users;
        }

        void setUsers(Map<String, Double> users) {
            this.users.putAll(users);
        }

        double getUsersDefault() {
            return usersDefault;
        }

        void setUsersDefault(double usersDefault) {
            this.usersDefault = usersDefault;
        }

    }

}
