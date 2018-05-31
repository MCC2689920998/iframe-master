package com.ppmoney.asset.iframe.iter;

import com.alibaba.fastjson.JSONObject;
import com.ppmoney.asset.iframe.entity.Identity;
import com.ppmoney.asset.iframe.util.IdentityBuilder;
import com.ppmoney.asset.itest.annotation.TestData;
import com.ppmoney.asset.itest.data.TestDataLoader;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static com.ppmoney.asset.iframe.util.IdentityBuilder.MetaIdentity;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by paul on 2018/5/10.
 */
@TestData("classpath:data/entity.json")
public class ContainerTest {
    @Test @TestData(node = "example")
    public void search() throws IOException {
        JSONObject model = TestDataLoader.loadTestData(JSONObject.class);
        Container container = new Container(model);
        Set<Object> result = container.search("person", MetaIdentity);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(hasSize(2)));

        Identity names = IdentityBuilder.build("id");
        Identity values = IdentityBuilder.build("310115200001011713");

        result = container.search("person", names.zip(values));
        assertThat(result, is(notNullValue()));
        assertThat(result, is(hasSize(1)));
    }
}