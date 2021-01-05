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

package com.navercorp.pinpoint.web.metric.dao.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.metric.util.QueryStatementWriter;
import com.navercorp.pinpoint.web.metric.mapper.pinot.PinotSystemMetricMapper;
import com.navercorp.pinpoint.web.metric.util.pinot.PinotQueryStatementWriter;
import com.navercorp.pinpoint.web.metric.vo.SampledSystemMetric;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.Request;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Repository
public class PinotSystemMetricDao implements SystemMetricDao {
    private static final String ZK_URL = "10.113.84.89:2191";
    private static final String PINOT_CLUSTER_NAME = "PinotCluster";
    private final Connection pinotConnection;
    private final PinotQueryStatementWriter queryStatementWriter;
    private final PinotSystemMetricMapper pinotSystemMetricMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public PinotSystemMetricDao(PinotQueryStatementWriter queryStatementWriter,
                                PinotSystemMetricMapper pinotSystemMetricMapper) {
        pinotConnection = ConnectionFactory.fromZookeeper(ZK_URL + "/" + PINOT_CLUSTER_NAME);
        this.queryStatementWriter = Objects.requireNonNull(queryStatementWriter, "queryStatementWriter");
        this.pinotSystemMetricMapper = Objects.requireNonNull(pinotSystemMetricMapper, "pinotResultSetProcessor");
    }

    @Override
    public List<String> getMetricNameList(String applicationName) {
        String queryLong = queryStatementWriter.queryForMetricNameList(applicationName, true);
        String queryDouble = queryStatementWriter.queryForMetricNameList(applicationName, false);
        ResultSet resultSetLong = queryAndGetResultSet(queryLong);
        ResultSet resultSetDouble = queryAndGetResultSet(queryDouble);

        return mergeList(pinotSystemMetricMapper.processStringList(resultSetLong),
                pinotSystemMetricMapper.processStringList(resultSetDouble));
    }

    @Override
    public List<String> getFieldNameList(String applicationName, String metricName) {
        String queryLong = queryStatementWriter.queryForFieldNameList(applicationName, metricName, true);
        String queryDouble = queryStatementWriter.queryForFieldNameList(applicationName, metricName, false);
        ResultSet resultSetLong = queryAndGetResultSet(queryLong);
        ResultSet resultSetDouble = queryAndGetResultSet(queryDouble);

        return mergeList(pinotSystemMetricMapper.processStringList(resultSetLong),
                pinotSystemMetricMapper.processStringList(resultSetDouble));
    }

    @Override
    public List<TagBo> getTagBoList(String applicationName, String metricName, String fieldName, boolean isLong) {
        ResultSet timestampResultSet = queryAndGetResultSet(queryStatementWriter.queryTimestampForField(applicationName, metricName, fieldName, isLong));
        long timestamp = timestampResultSet.getLong(0, 0);

        String query = queryStatementWriter.queryForTagBoList(applicationName, metricName, fieldName, isLong, timestamp);
        ResultSet resultSet = queryAndGetResultSet(query);

        return pinotSystemMetricMapper.processTagBoList(resultSet);
    }

    @Override
    public List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tags, boolean isLong, Range range) {
        String query = queryStatementWriter.queryForSystemMetricBoList(applicationName, metricName, fieldName, tags, isLong, range);
        ResultSet resultSet = queryAndGetResultSet(query);
        return pinotSystemMetricMapper.processSystemMetricBoList(resultSet, isLong);
    }

    @Override
    public List<SampledSystemMetric> getSampledSystemMetric(String applicationName, String metricName, String fieldName, List<TagBo> tags, boolean isLong, TimeWindow timeWindow) {
        String query = queryStatementWriter.queryForSampledSystemMetric(applicationName, metricName, fieldName, tags, isLong, timeWindow);
        ResultSet resultSet = queryAndGetResultSet(query);
        return pinotSystemMetricMapper.processSampledSystemMetric(resultSet, isLong);
    }

    private ResultSet queryAndGetResultSet(String query) {
        Request request = new Request("sql", query);
        ResultSetGroup resultSetGroup = pinotConnection.execute(request);
        return resultSetGroup.getResultSet(0);
    }

    private List<String> mergeList(List<String> list, List<String> listToAdd) {
        for (String s : listToAdd) {
            if (!list.contains(s)) {
                list.add(s);
            }
        }
        Collections.sort(list);

        return list;
    }
}
