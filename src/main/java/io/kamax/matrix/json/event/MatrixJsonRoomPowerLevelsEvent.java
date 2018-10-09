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
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.MatrixJsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java8.util.Optional;

public class MatrixJsonRoomPowerLevelsEvent extends MatrixJsonRoomEvent implements _RoomPowerLevelsEvent {

    private Content content;

    public MatrixJsonRoomPowerLevelsEvent(JsonObject obj) {
        super(obj);

        content = new Content(getObj("content"));
    }

    @Override
    public Optional<Double> getBan() {
        return Optional.ofNullable(content.getBan());
    }

    @Override
    public Map<String, Double> getEvents() {
        return Collections.unmodifiableMap(content.getEvents());
    }

    @Override
    public Optional<Double> getEventsDefault() {
        return Optional.ofNullable(content.getEventsDefault());
    }

    @Override
    public Optional<Double> getInvite() {
        return Optional.ofNullable(content.getInvite());
    }

    @Override
    public Optional<Double> getKick() {
        return Optional.ofNullable(content.getKick());
    }

    @Override
    public Optional<Double> getRedact() {
        return Optional.ofNullable(content.getRedact());
    }

    @Override
    public Optional<Double> getStateDefault() {
        return Optional.ofNullable(content.getStateDefault());
    }

    @Override
    public Map<String, Double> getUsers() {
        return Collections.unmodifiableMap(content.getUsers());
    }

    @Override
    public Optional<Double> getUsersDefault() {
        return Optional.ofNullable(content.getUsersDefault());
    }

    private class Content extends MatrixJsonObject {

        private Double ban;
        private Map<String, Double> events = new HashMap<>();
        private Double eventsDefault;
        private Double invite;
        private Double kick;
        private Double redact;
        private Double stateDefault;
        private Map<String, Double> users = new HashMap<>();
        private Double usersDefault;

        Content(JsonObject obj) {
            super(obj);

            setBan(getDoubleIfPresent("ban"));
            setEventsDefault(getDoubleIfPresent("events_default"));
            setInvite(getDoubleIfPresent("invite"));
            setKick(getDoubleIfPresent("kick"));
            setRedact(getDoubleIfPresent("redact"));
            setStateDefault(getDoubleIfPresent("state_default"));
            setUsersDefault(getDoubleIfPresent("users_default"));

            GsonUtil.findObj(obj, "events").ifPresent(eventsJson -> {
                Map<String, Double> eventsMap = eventsJson.entrySet().stream()
                        .collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().getAsDouble()));
                setEvents(eventsMap);
            });

            GsonUtil.findObj(obj, "users").ifPresent(usersJson -> {
                Map<String, Double> usersMap = usersJson.entrySet().stream()
                        .collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().getAsDouble()));
                setUsers(usersMap);
            });
        }

        Double getBan() {
            return ban;
        }

        void setBan(Double ban) {
            this.ban = ban;
        }

        Double getEventsDefault() {
            return eventsDefault;
        }

        void setEventsDefault(Double eventsDefault) {
            this.eventsDefault = eventsDefault;
        }

        Map<String, Double> getEvents() {
            return events;
        }

        void setEvents(Map<String, Double> events) {
            this.events.putAll(events);
        }

        Double getInvite() {
            return invite;
        }

        void setInvite(Double invite) {
            this.invite = invite;
        }

        Double getKick() {
            return kick;
        }

        void setKick(Double kick) {
            this.kick = kick;
        }

        Double getRedact() {
            return redact;
        }

        void setRedact(Double redact) {
            this.redact = redact;
        }

        Double getStateDefault() {
            return stateDefault;
        }

        void setStateDefault(Double stateDefault) {
            this.stateDefault = stateDefault;
        }

        Map<String, Double> getUsers() {
            return users;
        }

        void setUsers(Map<String, Double> users) {
            this.users.putAll(users);
        }

        Double getUsersDefault() {
            return usersDefault;
        }

        void setUsersDefault(Double usersDefault) {
            this.usersDefault = usersDefault;
        }

    }

}
