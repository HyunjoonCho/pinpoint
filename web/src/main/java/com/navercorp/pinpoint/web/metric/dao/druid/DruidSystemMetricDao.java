/*
 * Copyright 2020 NAVER Corp.
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

package com.navercorp.pinpoint.web.metric.dao.druid;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.metric.mapper.druid.DruidSystemMetricMapper;
import com.navercorp.pinpoint.web.metric.util.druid.DruidQueryStatementWriter;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Hyunjoon Cho
 */
@Repository
public class DruidSystemMetricDao implements SystemMetricDao {
    private final String druidUrl;
    private final Properties properties;
    private Connection connection;
    private final DruidQueryStatementWriter queryStatementWriter;
    private final DruidSystemMetricMapper systemMetricMapper;

    public DruidSystemMetricDao(DruidQueryStatementWriter queryStatementWriter,
                                DruidSystemMetricMapper systemMetricMapper) {
        this.queryStatementWriter = Objects.requireNonNull(queryStatementWriter, "queryStatementWriter");
        this.systemMetricMapper = Objects.requireNonNull(systemMetricMapper, "systemMetricMapper");

        this.druidUrl = "jdbc:avatica:remote:url=http://10.113.84.140:8082/druid/v2/sql/avatica/";
        this.properties = new Properties();
        properties.put("useApproximateTopN", "false");
        try {
            this.connection = DriverManager.getConnection(druidUrl, properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getMetricNameList(String applicationName) {
        try {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(queryStatementWriter.queryForMetricNameList(applicationName));
            return systemMetricMapper.processStringList(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getFieldNameList(String applicationName, String metricName) {
        try {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(queryStatementWriter.queryForFieldNameList(applicationName, metricName));
            return systemMetricMapper.processStringList(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TagBo> getTagBoList(String applicationName, String metricName, String fieldName) {
        try {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(queryStatementWriter.queryForTagBoList(applicationName, metricName, fieldName, 0));
            return systemMetricMapper.processTagBoList(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tags, Range range) {
        try {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(queryStatementWriter.queryForSystemMetricBoList(applicationName, metricName, fieldName, tags, range));
            return systemMetricMapper.processSystemMetricBoList(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PreDestroy
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
