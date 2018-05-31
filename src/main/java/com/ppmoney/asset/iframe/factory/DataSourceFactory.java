package com.ppmoney.asset.iframe.factory;


import lombok.Setter;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Created by paul on 2018/5/20.
 */
@Setter
public class DataSourceFactory implements FactoryBean<DataSource> {
    @NotNull private String driverClassName;

    @NotNull private String url;

    @NotNull private String username;

    @NotNull private String password;


    @Nullable
    @Override
    public DataSource getObject() throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }
}
