/*
 * Copyright 2021 DeNA Co., Ltd.
 *
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
package com.packetproxyhub.repository.sqlite;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class SqliteDataSourceFactory implements DataSourceFactory {

    private SQLiteConfig config;
    private SQLiteConnectionPoolDataSource dataSource;

    public SqliteDataSourceFactory() {
        config = new org.sqlite.SQLiteConfig();
        config.enforceForeignKeys(true);
    }

    @Override
    public void setProperties(Properties props) {
        dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setConfig(config);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
