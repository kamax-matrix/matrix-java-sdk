package io.kamax.matrix.room;

import com.google.gson.JsonElement;

import io.kamax.matrix._MatrixID;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Possible options that can be passed to the room creation call.
 */
public interface _RoomCreationOptions {

    /**
     * Get the room directory visibility.
     * 
     * @return the optional value.
     */
    Optional<String> getVisibility();

    /**
     * Get the desired room alias local part.
     * 
     * @return the optional value.
     */
    Optional<String> getAliasName();

    /**
     * Get the room name.
     * 
     * @return the optional value.
     */
    Optional<String> getName();

    /**
     * Get the room topic.
     * 
     * @return the optional value.
     */
    Optional<String> getTopic();

    /**
     * Get the list of user Matrix IDs to invite to the room.
     * 
     * @return the optional value.
     */
    Optional<Set<_MatrixID>> getInvites();

    /**
     * Get the extra keys to be added to the content of the m.room.create event.
     * 
     * @return the optional value.
     */
    Optional<Map<String, JsonElement>> getCreationContent();

    /**
     * Get the convenience parameter for setting various default state events.
     * 
     * @return the optional value.
     */
    Optional<String> getPreset();

    /**
     * What the is_direct flag on the m.room.member event for the invites should be set to.
     * 
     * @return the optional value.
     */
    Optional<Boolean> isDirect();

    /**
     * Get guest allowance to join the room.
     * 
     * @return the optional value.
     */
    Optional<Boolean> isGuestCanJoin();

}
