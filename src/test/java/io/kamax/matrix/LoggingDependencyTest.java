/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Arne Augenstein
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
package io.kamax.matrix;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

import java.net.URISyntaxException;

/**
 * A small test class, intended to verify that the logging framework has
 * a suitable logging implementation.
 */
public class LoggingDependencyTest {
    @Test
    public void login() throws URISyntaxException {
        if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
            Assert.fail("No logging implementation found, using fallback NOP logger");
        }
        Logger logger = LoggerFactory.getLogger("");
        logger.info("If you see this info in the logger, everything is alright");
    }
}
