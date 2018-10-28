/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sarl
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

package io.kamax.matrix.client;

import com.google.gson.JsonObject;

import io.kamax.matrix.event._MatrixAccountDataEvent;
import io.kamax.matrix.event._MatrixEphemeralEvent;
import io.kamax.matrix.event._MatrixPersistentEvent;
import io.kamax.matrix.event._MatrixStateEvent;

import java.util.List;
import java.util.Set;

/**
 * Representation of the data when performing a sync call on the Matrix Client API.
 */
public interface _SyncData {

    interface State {

        /**
         * The state of the room.
         * 
         * @return a list of state events.
         */
        List<_MatrixStateEvent> getEvents();

    }

    interface Timeline {

        /**
         * Events that happened in the sync window.
         * 
         * @return List of events.
         */
        List<_MatrixPersistentEvent> getEvents();

        /**
         * If the number of events returned was limited by the sync filter.
         * 
         * @return true if the number of events was limited, false if not.
         */
        boolean isLimited();

        /**
         * Token that can be supplied to fetch previous events for the associated room.
         * 
         * @return the token.
         */
        String getPreviousBatchToken();
    }

    interface Ephemeral {

        /**
         * Events that happened in the sync window.
         *
         * @return List of events.
         */
        List<_MatrixEphemeralEvent> getEvents();
    }

    interface AccountData {

        /**
         * Events that happened in the sync window.
         *
         * @return List of events.
         */
        List<_MatrixAccountDataEvent> getEvents();
    }

    interface InvitedRoom {

        /**
         * The ID of the room the user was invited to.
         * 
         * @return the room ID.
         */
        String getId();

        /**
         * The state of the room at the invite event.
         * 
         * @return a list of state events.
         */
        State getState();

    }

    interface UnreadNotifications {

        /**
         * The number of unread notifications with the highlight flag set.
         * 
         * @return the count.
         */
        long getHighlightCount();

        /**
         * The total number of unread notifications.
         * 
         * @return the count.
         */
        long getNotificationCount();

    }

    interface JoinedRoom {

        /**
         * The ID of the room the user is joined to.
         * 
         * @return the room id.
         */
        String getId();

        /**
         * State changes prior the start of the timeline.
         * 
         * @return a list of state events.
         */
        State getState();

        /**
         * The room timeline for this sync batch.
         * 
         * @return the timeline.
         */
        Timeline getTimeline();

        /**
         * Ephemeral events of the room.
         *
         * @return a list of ephemeral events.
         */
        Ephemeral getEphemeral();

        /**
         * Account events of the room.
         *
         * @return a list of account data events.
         */
        AccountData getAccountData();

        /**
         * The Counts of unread notifications.
         * 
         * @return unread notifications.
         */
        UnreadNotifications getUnreadNotifications();

    }

    interface LeftRoom {

        /**
         * The ID of the room the user is joined to.
         * 
         * @return the room id.
         */
        String getId();

        /**
         * State changes prior the start of the timeline.
         * 
         * @return a list of state events.
         */
        State getState();

        /**
         * The room timeline up to the leave event.
         * 
         * @return the timeline.
         */
        Timeline getTimeline();

    }

    interface Rooms {

        /**
         * Rooms the user was invited to within this sync window.
         * 
         * @return Set of InvitedRoom objects.
         */
        Set<InvitedRoom> getInvited();

        /**
         * Rooms the user was joined in within this sync window.
         * 
         * @return Set of JoinedRoom objects.
         */
        Set<JoinedRoom> getJoined();

        /**
         * Rooms the user left from within this sync window.
         * 
         * @return Set of LeftRoom objects.
         */
        Set<LeftRoom> getLeft();

    }

    /**
     * The batch token to supply in the next sync call.
     * 
     * @return the batch token.
     */
    String nextBatchToken();

    /**
     * The global private data created by this user.
     *
     * @return the account data.
     */
    AccountData getAccountData();

    /**
     * Update to the rooms.
     * 
     * @return rooms object.
     */
    Rooms getRooms();

    /**
     * The raw JSON data for this object.
     * 
     * @return the JSON data.
     */
    JsonObject getJson();

}
