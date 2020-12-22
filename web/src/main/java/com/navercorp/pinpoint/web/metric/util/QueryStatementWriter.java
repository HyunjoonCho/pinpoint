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

package com.navercorp.pinpoint.web.metric.util;

import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;

/**
 * @author Hyunjoon Cho
 */
public abstract class QueryStatementWriter {
    public abstract String queryForMetricNameList(String applicationName);
    public abstract String queryForFieldNameList(String applicationName, String metricName);
    public abstract String queryForTagBoList(String applicationName, String metricName, String fieldName, long timestamp);
    public abstract String queryForSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tagBos, Range range);

    protected StringBuilder buildBasicQuery(boolean distinct, String target, String db) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        sb.append(target);
        sb.append(" FROM ").append(db);
        // systemMetric for Pinot
        // \"system-metric\" for Druid
        return sb;
    }

    protected StringBuilder addWhereStatement(StringBuilder query, String key, String value) {
        return query.append(" WHERE ").append(key).append("='").append(value).append("'");
    }

    protected StringBuilder addAndStatement(StringBuilder query, String key, String value) {
        return query.append(" AND ").append(key).append("='").append(value).append("'");
    }

    protected StringBuilder addAndStatement(StringBuilder query, String key, long value) {
        return query.append(" AND ").append(key).append("=").append(value);
    }

//    private StringBuilder addOrStatement(StringBuilder query, String key, String value) {
//        return query.append(" OR ").append(key).append("='").append(value).append("'");
//    }

    protected StringBuilder setLimit(StringBuilder query, long limit) {
        return query.append(" LIMIT ").append(limit);
    }

}
