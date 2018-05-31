package com.ppmoney.asset.iframe.util;

import com.alibaba.fastjson.JSONObject;
import com.ppmoney.asset.itest.annotation.TestData;
import com.ppmoney.asset.itest.data.TestDataLoader;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by paul on 2018/5/8.
 */
@TestData("classpath:data/entity.json")
public class ReflectionTest {
    /***
     * Test for get method.
     */
    @Test @TestData(node = "simple")
    public void searchSimple() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        Object object = Reflection.get(any, "id");
        assertThat(object, is("123456"));
    }

    @Test @TestData(node = "element_array")
    public void searchWhenElementIsArray() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        Object object = Reflection.get(any, "id");
    }

    @Test @TestData(node = "simple_double")
    public void searchSimpleDouble() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        Object object = Reflection.get(any, "value");
        assertThat(object, is(123456));
    }

    @Test @TestData(node = "simple_null")
    public void searchSimpleNull() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        Object object = Reflection.get(any, "value");
        assertThat(object, is(nullValue()));
    }

    @Test @TestData(node = "simple_nothing")
    public void searchSimpleNothing() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        Object nothing = Reflection.get(any, "value");
        assertThat(nothing, is(nullValue()));
    }

    @Test @TestData(node = "embedded")
    public void searchEmbedded() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        Object id = Reflection.get(any, "person.id");
        assertThat(id, is("342622199011023452"));
    }

    @Test @TestData(node = "embedded_array")
    public void searchEmbeddedArray() throws IOException {
        JSONObject any = TestDataLoader.loadTestData((JSONObject.class));
        Object id = Reflection.get(any, "person.id");
        assertThat(id, is("342622199011023452"));
    }


    @Test @TestData(node = "embedded_array")
    public void searchEmbeddedAll() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        List<Object> result = Reflection.getAll(any, "person.id");

        assertThat(result, hasItem("342622199011023452"));
    }

    @Test @TestData(node = "embedded_arrays")
    public void searchComplicatedObject() throws IOException {
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        Object result = Reflection.get(any, "person");

        // Then
        assertThat(result, is(instanceOf(Map.class)));
        Object id = ((Map)result).get("id");
        assertThat(id, is("342622199011023452"));
    }

    /**
     * Test for set method.
     */
    @Test @TestData(node = "simple")
    public void setSimpleValue() throws IOException {
        // Given
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);

        // When
        Reflection.set(any, "key", "value");
        Object value = Reflection.get(any, "key");

        // Then
        assertThat(value, is("value"));
    }

    @Test @TestData(node = "embedded")
    public void setEmbeddedValue() throws IOException {
        // Given
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        // When
        Reflection.set(any, "person.key", "value");
        Object value = Reflection.get(any, "person.key");

        // Then
        assertThat(value, is("value"));
    }

    @Test @TestData(node = "embedded")
    public void setEmbeddedComplicatedObject() throws IOException {
        // Given
        JSONObject any = TestDataLoader.loadTestData(JSONObject.class);
        // When
        Map<Object, Object> complicatedObject = new HashMap<Object, Object>();
        complicatedObject.put("cat", "Hello Ketty");
        Reflection.set(any, "person.key", complicatedObject);
        Object testObject = Reflection.get(any, "person.key");

        // Then
        assertThat(testObject, is(instanceOf(Map.class)));
        assertThat(((Map)testObject).get("cat"), is("Hello Ketty"));
    }
}