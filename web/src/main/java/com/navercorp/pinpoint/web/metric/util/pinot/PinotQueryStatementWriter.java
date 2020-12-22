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
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Hyunjoon Cho
 */
//@Component
public class PinotQueryStatementWriter extends QueryStatementWriter {

    @Override
    public String queryForMetricNameList(String applicationName) {
        StringBuilder queryStatement = addWhereStatement(buildBasicQuery(true, "metricName", "systemMetric"), "applicationName", applicationName);
        return queryStatement.toString();
    }

    @Override
    public String queryForFieldNameList(String applicationName, String metricName) {
        StringBuilder queryStatement = setLimit(
                addAndStatement(
                        addWhereStatement(
                                buildBasicQuery(true, "fieldName", "systemMetric"),
                                "applicationName", applicationName),
                        "metricName", metricName),
                20);

        return queryStatement.toString();
    }

    public String queryTimestampForField(String applicationName, String metricName, String fieldName) {
        StringBuilder queryStatement = setLimit(
                addAndStatement(
                        addAndStatement(
                                addWhereStatement(
                                        buildBasicQuery(true, "timestampInEpoch", "systemMetric"),
                                        "applicationName", applicationName),
                                "metricName", metricName),
                        "fieldName", fieldName),
                1);

        return queryStatement.toString();
    }

    @Override
    public String queryForTagBoList(String applicationName, String metricName, String fieldName, long timestamp) {
        StringBuilder queryStatement = addAndStatement(
                addAndStatement(
                        addAndStatement(
                                addWhereStatement(
                                        buildBasicQuery(false, "tagName, tagValue", "systemMetric"),
                                        "applicationName", applicationName),
                                "metricName", metricName),
                        "fieldName", fieldName),
                "timestampInEpoch", timestamp);

        return queryStatement.toString();
    }

    @Override
    public String queryForSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tagBos, Range range) {
        StringBuilder queryStatement = addAndStatement(
                addAndStatement(
                        addWhereStatement(
                                buildBasicQuery(false, "*", "systemMetric"),
                                "applicationName", applicationName),
                        "metricName", metricName),
                "fieldName", fieldName);

        for (TagBo tagBo : tagBos) {
            queryStatement = addAndStatement(queryStatement, "tagName", tagBo.getTagName());
            queryStatement = addAndStatement(queryStatement, "tagValue", tagBo.getTagValue());
        }

        queryStatement = addRangeStatement(queryStatement, range);

        long expectedLimit = (range.getTo() - range.getFrom())/10000 - 1;
        // by default, telegraf collect every 10sec = 10000ms
        // make it configurable

        queryStatement = setLimit(queryStatement, expectedLimit);

        return queryStatement.toString();
    }

    private StringBuilder addRangeStatement(StringBuilder query, Range range) {

        return query.append(" AND ").append("timestampInEpoch").append(" >= ").append(range.getFrom())
                .append(" AND ").append("timestampInEpoch").append(" <= ").append(range.getTo());
    }
}
