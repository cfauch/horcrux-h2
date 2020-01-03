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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.code.fauch.horcrux.DataBase;
import com.code.fauch.horcrux.ECreateOption;

/**
 * @author c.fauch
 *
 */
public class H2HorcruxTest {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testEmptyBaseFirstVersion() throws URISyntaxException, SQLException, IOException {
        final Properties prop = new Properties();
        prop.setProperty("jdbcUrl", "jdbc:h2:mem:v1;DB_CLOSE_DELAY=-1");
        prop.setProperty("username", "harry");
        prop.setProperty("password", "");
        final Path scripts = Paths.get(getClass().getResource("/dataset/v1").toURI());
        try(DataBase db = DataBase.init("pool").withScripts(scripts).versionTable("HORCRUX_VERSIONS").build(prop)) {
            db.open(ECreateOption.SCHEMA);
            Assert.assertEquals(1, db.getCurrentVersion().intValue());
        }
    }

    @Test
    public void testEmptyBaseV2() throws URISyntaxException, SQLException, IOException {
        final Properties prop = new Properties();
        prop.setProperty("jdbcUrl", "jdbc:h2:mem:v2;DB_CLOSE_DELAY=-1");
        prop.setProperty("username", "harry");
        prop.setProperty("password", "");
        final Path scripts = Paths.get(getClass().getResource("/dataset/v2").toURI());
        try(DataBase db = DataBase.init("pool").withScripts(scripts).versionTable("HORCRUX_VERSIONS").build(prop)) {
            db.open(ECreateOption.SCHEMA);
            Assert.assertEquals(2, db.getCurrentVersion().intValue());
            try(Connection conn = db.openSession()) {
                final DatabaseMetaData meta = conn.getMetaData();
                final ResultSet tables = meta.getTables(null, null, "HORCRUX_USERS", null);
                Assert.assertTrue(tables.next());
                final ResultSet columns = meta.getColumns(null, null, "HORCRUX_USERS", null);
                final ArrayList<String> colNames = new ArrayList<>();
                while(columns.next()) {
                    colNames.add(columns.getString("COLUMN_NAME"));
                }
                Assert.assertEquals(Arrays.asList("ID", "NAME", "PROFILE"), colNames);
            }
        }
    }
    
    @Test
    public void testV1ToV2Upgrade() throws URISyntaxException, SQLException, IOException {
        final Path dbFile = this.folder.getRoot().toPath().resolve("yo.mv.db");
        Files.copy(Paths.get(getClass().getResource("/dataset/v1/v1.mv.db").toURI()), dbFile);
        final String url = String.format("jdbc:h2:%s", this.folder.getRoot().toURI().resolve("yo"));
        final Properties prop = new Properties();
        prop.setProperty("jdbcUrl", url);
        prop.setProperty("username", "harry");
        prop.setProperty("password", "");
        prop.setProperty("autoCommit", "false");
        prop.setProperty("poolName", "database-connection-pool");
        final Path scripts = Paths.get(getClass().getResource("/dataset/v2").toURI());
        try(DataBase db = DataBase.init("pool").withScripts(scripts).versionTable("HORCRUX_VERSIONS").build(prop)) {
            final Integer version = db.open(ECreateOption.UPGRADE);
            Assert.assertEquals(2, version.intValue());
            try(Connection conn = db.openSession()) {
                final DatabaseMetaData meta = conn.getMetaData();
                final ResultSet tables = meta.getTables(null, null, "HORCRUX_USERS", null);
                Assert.assertTrue(tables.next());
                final ResultSet columns = meta.getColumns(null, null, "HORCRUX_USERS", null);
                final ArrayList<String> colNames = new ArrayList<>();
                while(columns.next()) {
                    colNames.add(columns.getString("COLUMN_NAME"));
                }
                Assert.assertEquals(Arrays.asList("ID", "NAME", "PROFILE"), colNames);
            }
        }
    }
    
    @Test
    public void testEmptyBaseV3() throws URISyntaxException, SQLException, IOException {
        final Properties prop = new Properties();
        prop.setProperty("jdbcUrl", "jdbc:h2:mem:v3;DB_CLOSE_DELAY=-1");
        prop.setProperty("username", "harry");
        prop.setProperty("password", "");
        final Path scripts = Paths.get(getClass().getResource("/dataset/v3").toURI());
        try(DataBase db = DataBase.init("pool").withScripts(scripts).versionTable("HORCRUX_VERSIONS").build(prop)) {
            final Integer version = db.open(ECreateOption.SCHEMA);
            Assert.assertEquals(3, version.intValue());
            try(Connection conn = db.openSession()) {
                final DatabaseMetaData meta = conn.getMetaData();
                final ResultSet tables = meta.getTables(null, null, "HORCRUX_USERS", null);
                Assert.assertTrue(tables.next());
                final ResultSet columns = meta.getColumns(null, null, "HORCRUX_USERS", null);
                final ArrayList<String> colNames = new ArrayList<>();
                while(columns.next()) {
                    colNames.add(columns.getString("COLUMN_NAME"));
                }
                Assert.assertEquals(Arrays.asList("ID", "NAME", "PROFILE", "EMAIL"), colNames);
            }
        }
    }
    
    @Test
    public void testV1ToV3Upgrade() throws URISyntaxException, SQLException, IOException {
        final Path dbFile = this.folder.getRoot().toPath().resolve("yo.mv.db");
        Files.copy(Paths.get(getClass().getResource("/dataset/v1/v1.mv.db").toURI()), dbFile);
        final String url = String.format("jdbc:h2:%s", this.folder.getRoot().toURI().resolve("yo"));
        final Properties prop = new Properties();
        prop.setProperty("jdbcUrl", url);
        prop.setProperty("username", "harry");
        prop.setProperty("password", "");
        final Path scripts = Paths.get(getClass().getResource("/dataset/v3").toURI());
        try(DataBase db = DataBase.init("pool").withScripts(scripts).versionTable("HORCRUX_VERSIONS").build(prop)) {
            final Integer version = db.open(ECreateOption.UPGRADE);
            Assert.assertEquals(3, version.intValue());
            try(Connection conn = db.openSession()) {
                final DatabaseMetaData meta = conn.getMetaData();
                final ResultSet tables = meta.getTables(null, null, "HORCRUX_USERS", null);
                Assert.assertTrue(tables.next());
                final ResultSet columns = meta.getColumns(null, null, "HORCRUX_USERS", null);
                final ArrayList<String> colNames = new ArrayList<>();
                while(columns.next()) {
                    colNames.add(columns.getString("COLUMN_NAME"));
                }
                Assert.assertEquals(Arrays.asList("ID", "NAME", "PROFILE", "EMAIL"), colNames);
            }
        }
    }
    
    @Test
    public void testV2ToV3Upgrade() throws URISyntaxException, SQLException, IOException {
        final Path dbFile = this.folder.getRoot().toPath().resolve("yo.mv.db");
        Files.copy(Paths.get(getClass().getResource("/dataset/v2/v2.mv.db").toURI()), dbFile);
        final String url = String.format("jdbc:h2:%s", this.folder.getRoot().toURI().resolve("yo"));
        final Properties prop = new Properties();
        prop.setProperty("jdbcUrl", url);
        prop.setProperty("username", "harry");
        prop.setProperty("password", "");
        final Path scripts = Paths.get(getClass().getResource("/dataset/v3").toURI());
        try(DataBase db = DataBase.init("pool").withScripts(scripts).versionTable("HORCRUX_VERSIONS").build(prop)) {
            final Integer version = db.open(ECreateOption.UPGRADE);
            Assert.assertEquals(3, version.intValue());
            try(Connection conn = db.openSession()) {
                final DatabaseMetaData meta = conn.getMetaData();
                final ResultSet tables = meta.getTables(null, null, "HORCRUX_USERS", null);
                Assert.assertTrue(tables.next());
                final ResultSet columns = meta.getColumns(null, null, "HORCRUX_USERS", null);
                final ArrayList<String> colNames = new ArrayList<>();
                while(columns.next()) {
                    colNames.add(columns.getString("COLUMN_NAME"));
                }
                Assert.assertEquals(Arrays.asList("ID", "NAME", "PROFILE", "EMAIL"), colNames);
            }
        }
    }

}
