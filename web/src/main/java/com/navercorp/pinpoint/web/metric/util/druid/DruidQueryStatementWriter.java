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

package com.navercorp.pinpoint.web.metric.util.druid;

import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.util.QueryStatementWriter;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Hyunjoon Cho
 */
@Component
public class DruidQueryStatementWriter extends QueryStatementWriter {
    private final SimpleDateFormat format;
    public DruidQueryStatementWriter() {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public String queryForMetricNameList(String applicationName) {
        StringBuilder queryStatement = addWhereStatement(buildBasicQuery(true, "metricName", "\"system-metric\""), "applicationName", applicationName);
        return queryStatement.toString();
    }

    @Override
    public String queryForFieldNameList(String applicationName, String metricName) {
        StringBuilder queryStatement = addAndStatement(
                        addWhereStatement(
                                buildBasicQuery(true, "fieldName", "\"system-metric\""),
                                "applicationName", applicationName),
                        "metricName", metricName);

        return queryStatement.toString();

    }


    @Override
    public String queryForTagBoList(String applicationName, String metricName, String fieldName, long timestamp) {
        StringBuilder queryStatement = addAndStatement(
                        addAndStatement(
                                addWhereStatement(
                                        buildBasicQuery(true, "tags", "\"system-metric\""),
                                        "applicationName", applicationName),
                                "metricName", metricName),
                        "fieldName", fieldName);

        return queryStatement.toString();
    }

    @Override
    public String queryForSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tagBos, Range range) {
        StringBuilder queryStatement = addAndStatement(
                addAndStatement(
                        addWhereStatement(
                                buildBasicQuery(false, "*", "\"system-metric\""),
                                "applicationName", applicationName),
                        "metricName", metricName),
                "fieldName", fieldName);

        for (TagBo tagBo : tagBos) {
            queryStatement = addContainsStringStatement(queryStatement, "tags", tagBo.toString());
        }

        queryStatement = addRangeStatement(queryStatement, range);

        return queryStatement.toString();
    }

    private StringBuilder addContainsStringStatement(StringBuilder query, String key, String value) {
        return query.append(" AND ").append("CONTAINS_STRING(").append(key).append(",'").append(value).append("')");
    }

    private StringBuilder addRangeStatement(StringBuilder query, Range range) {
        return query.append(" AND ").append("__time").append(" >= '").append(format.format(new Date(range.getFrom()))).append('\'')
                .append(" AND ").append("__time").append(" <= '").append(format.format(new Date(range.getTo()))).append('\'');
    }
}
