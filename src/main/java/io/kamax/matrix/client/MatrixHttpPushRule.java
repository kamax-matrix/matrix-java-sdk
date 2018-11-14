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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.kamax.matrix.json.GsonUtil;

import org.apache.commons.lang3.ArrayUtils;

import java.net.URL;
import java.util.List;

import javax.swing.*;

public class MatrixHttpPushRule extends AMatrixHttpClient implements _PushRule {

    private static final String ActionKey = "actions";
    private static final String EnabledKey = "enabled";

    private final String[] baseSegments;

    public MatrixHttpPushRule(MatrixClientContext context, String scope, String kind, String id) {
        super(context);
        baseSegments = new String[] { "pushrules", scope, kind, id };
    }

    private URL makeUrl() {
        return getClientPath(baseSegments);
    }

    private URL makeUrl(String... segments) {
        return getClientPath(ArrayUtils.addAll(baseSegments, segments));
    }

    @Override
    public JsonObject getJson() {
        return GsonUtil.parseObj(executeAuthenticated(getRequest(makeUrl())));
    }

    @Override
    public void set(JsonObject data) {
        executeAuthenticated(request(makeUrl()).put(getJsonBody(data)));
    }

    @Override
    public void delete() {
        executeAuthenticated(request(makeUrl()).delete());
    }

    @Override
    public boolean isEnabled() {
        JsonObject response = GsonUtil.parseObj(executeAuthenticated(getRequest(makeUrl(EnabledKey))));
        return GsonUtil.getPrimitive(response, EnabledKey).getAsBoolean();
    }

    @Override
    public void setEnabled(boolean enabled) {
        executeAuthenticated(request(makeUrl(EnabledKey)).put(getJsonBody(GsonUtil.makeObj(EnabledKey, enabled))));
    }

    @Override
    public List<String> getActions() {
        JsonObject response = GsonUtil.parseObj(executeAuthenticated(getRequest(makeUrl(ActionKey))));
        return GsonUtil.asList(GsonUtil.findArray(response, ActionKey).orElseGet(JsonArray::new), String.class);
    }

    @Override
    public void setActions(List<String> data) {
        executeAuthenticated(
                request(makeUrl(ActionKey)).put(getJsonBody(GsonUtil.makeObj(ActionKey, GsonUtil.asArray(data)))));
    }

}
