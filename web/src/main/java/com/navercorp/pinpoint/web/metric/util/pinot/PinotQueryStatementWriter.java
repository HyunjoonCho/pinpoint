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

package com.navercorp.pinpoint.web.metric.util.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.util.QueryStatementWriter;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Hyunjoon Cho
 */
@Component
public class PinotQueryStatementWriter extends QueryStatementWriter {

    private final static String LONG_DB = "systemMetricLong";
    private final static String DOUBLE_DB = "systemMetricDouble";

    @Override
    public String queryForMetricNameList(String applicationName, boolean isLong) {
        String db = isLong? LONG_DB : DOUBLE_DB;
        StringBuilder queryStatement = addWhereStatement(buildBasicQuery(true, "metricName", db), "applicationName", applicationName);
        return queryStatement.toString();
    }

    @Override
    public String queryForFieldNameList(String applicationName, String metricName, boolean isLong) {
        String db = isLong? LONG_DB : DOUBLE_DB;
        StringBuilder queryStatement = setLimit(
                addAndStatement(
                        addWhereStatement(
                                buildBasicQuery(true, "fieldName", db),
                                "applicationName", applicationName),
                        "metricName", metricName),
                20);

        return queryStatement.toString();
    }

    public String queryTimestampForField(String applicationName, String metricName, String fieldName, boolean isLong) {
        String db = isLong? LONG_DB : DOUBLE_DB;
        StringBuilder queryStatement = setLimit(
                addAndStatement(
                        addAndStatement(
                                addWhereStatement(
                                        buildBasicQuery(true, "timestampInEpoch", db),
                                        "applicationName", applicationName),
                                "metricName", metricName),
                        "fieldName", fieldName),
                1);

        return queryStatement.toString();
    }

    @Override
    public String queryForTagBoList(String applicationName, String metricName, String fieldName, boolean isLong, long timestamp) {
        String db = isLong? LONG_DB : DOUBLE_DB;
        StringBuilder queryStatement = addAndStatement(
                addAndStatement(
                        addAndStatement(
                                addWhereStatement(
                                        buildBasicQuery(false, "tagName, tagValue", db),
                                        "applicationName", applicationName),
                                "metricName", metricName),
                        "fieldName", fieldName),
                "timestampInEpoch", timestamp);

        return queryStatement.toString();
    }

    @Override
    public String queryForSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tagBos, boolean isLong, Range range) {
        StringBuilder queryStatement = basicStatementForSystemMetric(applicationName, metricName, fieldName, tagBos, isLong, range);

        long expectedLimit = ((range.getTo() - range.getFrom())/10000 - 1) * 10;
        // by default, telegraf collect every 10sec = 10000ms
        // make it configurable
        queryStatement = setLimit(queryStatement, expectedLimit);

        return queryStatement.toString();
    }

    @Override
    public String queryForSampledSystemMetric(String applicationName, String metricName, String fieldName, List<TagBo> tagBos, boolean isLong, TimeWindow timeWindow) {
        Range range = timeWindow.getWindowRange();
        StringBuilder queryStatement = basicStatementForSystemMetric(applicationName, metricName, fieldName, tagBos, isLong, range);

        queryStatement = addSamplingCondition(queryStatement, timeWindow.getWindowSlotSize());

        long expectedLimit = ((range.getTo() - range.getFrom())/10000 - 1) * 10;
        queryStatement = setLimit(queryStatement, expectedLimit);

        return queryStatement.toString();
    }

    private StringBuilder basicStatementForSystemMetric(String applicationName, String metricName, String fieldName, List<TagBo> tagBos, boolean isLong, Range range) {
        String db = isLong? LONG_DB : DOUBLE_DB;
        StringBuilder queryStatement = addAndStatement(
                addAndStatement(
                        addWhereStatement(
                                buildBasicQuery(false, "*", db),
                                "applicationName", applicationName),
                        "metricName", metricName),
                "fieldName", fieldName);

        for (TagBo tagBo : tagBos) {
            queryStatement = addAndStatement(queryStatement, "tagName", tagBo.getTagName());
            queryStatement = addAndStatement(queryStatement, "tagValue", tagBo.getTagValue());
        }

        queryStatement = addRangeStatement(queryStatement, range);

        return queryStatement;
    }

    private StringBuilder addRangeStatement(StringBuilder query, Range range) {

        return query.append(" AND ").append("timestampInEpoch").append(" >= ").append(range.getFrom())
                .append(" AND ").append("timestampInEpoch").append(" <= ").append(range.getTo());
    }

    private StringBuilder addSamplingCondition(StringBuilder query, long intervalMs) {
        if (intervalMs == 10000L) {
            return query;
        }
        return query.append(" AND ").append("timestampInEpoch").append(" % ").append(intervalMs).append(" = 0");
    }
}
