package com.ppmoney.asset.iframe.entity;

import com.ppmoney.asset.iframe.util.IdentityBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by paul on 2018/5/10.
 */
public class IdentityTest {
    @Test
    public void IdentityWithNoSerial() {
        Identity identity = IdentityBuilder.build();
        assertThat(identity, is(notNullValue()));
    }
}