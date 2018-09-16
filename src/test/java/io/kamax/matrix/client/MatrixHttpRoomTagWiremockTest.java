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

package io.kamax.matrix.client;

import io.kamax.matrix.client.regular.MatrixHttpClient;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

public class MatrixHttpRoomTagWiremockTest extends AMatrixHttpRoomTagTest {
    protected String roomId = "roomId892347847";

    private String tagsBaseUrl = "/_matrix/client/r0/user/" + user.getId() + "/rooms/" + roomId + "/tags";

    private String tagRequestUrl = tagsBaseUrl + "?access_token=" + testToken;
    private String emptyTagsBody = "{\"tags\": " + "{}}";
    private String allTagsResponse = "{\"tags\": " + "{\"m.favourite\": {}," + "\"m.lowpriority\": {}," + "\"u."
            + testTag + "\": {}," + "\"u." + testTagWithOrder + "\": {\"order\": " + testTagOrder + "}}}";

    private String favouriteTagUrl = tagsBaseUrl + "/m.favourite?access_token=" + testToken;
    private String lowPriorityTagUrl = tagsBaseUrl + "/m.lowpriority?access_token=" + testToken;
    private String userTagUrl = tagsBaseUrl + "/u." + testTag + "?access_token=" + testToken;
    private String userTagWithOrderUrl = tagsBaseUrl + "/u." + testTagWithOrder + "?access_token=" + testToken;

    @Before
    public void createClient() {
        client = new MatrixHttpClient(getOrCreateClientContext());
    }

    @Override
    public void login() {
    }

    @Override
    public void logout() {
    }

    @Test
    @Override
    public void setAndReadTags() {
        createPreparationStubs();

        stubFor(put(urlEqualTo(favouriteTagUrl)).inScenario("Tags").whenScenarioStateIs("PreparationFinished")
                .willSetStateTo("FavouriteTagAdded").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(put(urlEqualTo(lowPriorityTagUrl)).inScenario("Tags").whenScenarioStateIs("FavouriteTagAdded")
                .willSetStateTo("LowPriorityTagAdded").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(put(urlEqualTo(userTagUrl)).inScenario("Tags").whenScenarioStateIs("LowPriorityTagAdded")
                .willSetStateTo("UserTagAdded").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(put(urlEqualTo(userTagWithOrderUrl)).inScenario("Tags").whenScenarioStateIs("UserTagAdded")
                .willSetStateTo("UserTagWithOrderAdded").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(get(urlEqualTo(tagRequestUrl)).inScenario("Tags").whenScenarioStateIs("UserTagWithOrderAdded")
                .willReturn(aResponse().withStatus(200).withBody(allTagsResponse)));

        stubFor(delete(urlEqualTo(favouriteTagUrl)).inScenario("Tags").whenScenarioStateIs("UserTagWithOrderAdded")
                .willSetStateTo("FavouriteTagDeleted").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(delete(urlEqualTo(lowPriorityTagUrl)).inScenario("Tags").whenScenarioStateIs("FavouriteTagDeleted")
                .willSetStateTo("LowPriorityTagDeleted").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(delete(urlEqualTo(userTagUrl)).inScenario("Tags").whenScenarioStateIs("LowPriorityTagDeleted")
                .willSetStateTo("UserTagDeleted").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(delete(urlEqualTo(userTagWithOrderUrl)).inScenario("Tags").whenScenarioStateIs("UserTagDeleted")
                .willSetStateTo("UserTagWithOrderDeleted").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(get(urlEqualTo(tagRequestUrl)).inScenario("Tags").whenScenarioStateIs("UserTagWithOrderDeleted")
                .willReturn(aResponse().withStatus(200).withBody(emptyTagsBody)));

        super.setAndReadTags();
    }

    @Test
    @Override
    public void setTagWithOrderOutOfRange() {
        createPreparationStubs();
        super.setTagWithOrderOutOfRange();
    }

    @Override
    public void readTagsFromSync() {
        createPreparationStubs();
        stubFor(put(urlEqualTo(favouriteTagUrl)).inScenario("Tags").whenScenarioStateIs("PreparationFinished")
                .willSetStateTo("FavouriteTagAdded").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(put(urlEqualTo(userTagUrl)).inScenario("Tags").whenScenarioStateIs("FavouriteTagAdded")
                .willSetStateTo("UserTagAdded").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(get(urlEqualTo("/_matrix/client/r0/sync?timeout=30000&access_token=" + testToken)).inScenario("Tags")
                .whenScenarioStateIs("UserTagAdded").willSetStateTo("syncExecuted")
                .willReturn(aResponse().withStatus(200).withBody(getSyncJson())));

        stubFor(delete(urlEqualTo(favouriteTagUrl)).inScenario("Tags").whenScenarioStateIs("syncExecuted")
                .willSetStateTo("FavouriteTagDeleted").willReturn(aResponse().withStatus(200).withBody("{}")));

        stubFor(delete(urlEqualTo(userTagUrl)).inScenario("Tags").whenScenarioStateIs("FavouriteTagDeleted")
                .willSetStateTo("UserTagDeleted").willReturn(aResponse().withStatus(200).withBody("{}")));

        super.readTagsFromSync();
    }

    private String getSyncJson() {
        try {
            InputStream is = new FileInputStream("src/test/resources/json/client/syncWithTags.json");
            return IOUtils.toString(is, StandardCharsets.UTF_8).replace("<roomId>", roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createPreparationStubs() {
        stubFor(get(urlEqualTo("/_matrix/client/r0/joined_rooms?access_token=" + testToken))
                .willReturn(aResponse().withStatus(200).withBody("{\"joined_rooms\": []}")));
        stubFor(post(urlEqualTo("/_matrix/client/r0/createRoom?access_token=" + testToken))
                .willReturn(aResponse().withStatus(200).withBody("{\"room_id\": \"" + roomId + "\"}")));

        stubFor(get(urlEqualTo(tagRequestUrl)).inScenario("Tags").whenScenarioStateIs(STARTED)
                .willSetStateTo("PreparationStep1").willReturn(aResponse().withStatus(200).withBody(emptyTagsBody)));

        stubFor(get(urlEqualTo(tagRequestUrl)).inScenario("Tags").whenScenarioStateIs("PreparationStep1")
                .willSetStateTo("PreparationStep2").willReturn(aResponse().withStatus(200).withBody(emptyTagsBody)));

        stubFor(get(urlEqualTo(tagRequestUrl)).inScenario("Tags").whenScenarioStateIs("PreparationStep2")
                .willSetStateTo("PreparationStep3").willReturn(aResponse().withStatus(200).withBody(emptyTagsBody)));

        stubFor(get(urlEqualTo(tagRequestUrl)).inScenario("Tags").whenScenarioStateIs("PreparationStep3")
                .willSetStateTo("PreparationFinished").willReturn(aResponse().withStatus(200).withBody(emptyTagsBody)));

    }

}
