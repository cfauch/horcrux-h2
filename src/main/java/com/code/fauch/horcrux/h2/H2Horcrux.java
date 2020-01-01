/*
 * Copyright 2019 Claire Fauch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.code.fauch.horcrux.h2;

import java.util.Properties;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;

import com.code.fauch.horcrux.spi.IHorcrux;

/**
 * horcrux implementation that provides a JDBC connection pool based on the simple one
 * included in H2.
 * 
 * @author c.fauch
 *
 */
public final class H2Horcrux implements IHorcrux {

    /**
     * The implemented horcrux type.
     */
    private static final String TYPE = "pool";

    /**
     * Creates and returns H2 connection pool.
     */
    @Override
    public DataSource newDataSource(final Properties properties) {
        return JdbcConnectionPool.create(
                properties.getProperty("jdbcUrl"), 
                properties.getProperty("username"), 
                properties.getProperty("password"));
    }

    /**
     * Close all connection of the H2 connection pool.
     */
    @Override
    public void close(final DataSource ds) {
        ((JdbcConnectionPool)ds).dispose();
    }

    /**
     * It's a "pool" implementation.
     */
    @Override
    public boolean accept(String type) {
        return TYPE.equals(type);
    }

}

