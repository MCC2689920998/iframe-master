package com.ppmoney.asset.iframe.cache;

import com.ppmoney.asset.iframe.entity.Identity;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * Created by paul on 2018/5/10.
 */
public class GlobalCacheTest {
    @Test
    public void cacheObject() {
        GlobalCache cache = GlobalCache.getInstance();
        Identity identity = cache.put(new Integer(1));

        int intv = cache.get(identity, Integer.class);
        assertThat(intv, is(1));

        cache.remove(identity);

        Integer intv2 = cache.get(identity, Integer.class);
        assertThat(intv2, is(nullValue()));
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Statement stmt = null;

        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost/stg", "ppmoney", "ppmoney");
        connection.setAutoCommit(false);
        System.out.println("Opened database successfully");

        stmt = connection.createStatement();
        String sql = "CREATE TABLE STUDENT " +
                "(ID TEXT PRIMARY KEY     NOT NULL ," +
                " NAME            TEXT    NOT NULL, " +
                " SEX             TEXT    NOT NULL, " +
                " AGE             TEXT    NOT NULL)";
        stmt.executeUpdate(sql);
        System.out.println("Table created successfully");

        stmt.close();
        connection.commit();
        connection.close();
    }
}