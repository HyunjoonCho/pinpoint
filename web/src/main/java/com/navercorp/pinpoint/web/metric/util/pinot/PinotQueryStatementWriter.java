package com.navercorp.pinpoint.web.metric.util.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.util.QueryStatementWriter;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;

public class PinotQueryStatementWriter implements QueryStatementWriter {

    @Override
    public String queryForMetricNameList(String applicationName) {
        StringBuilder queryStatement = addWhereStatement(buildBasicQuery(true, "metricName"), "applicationName", applicationName);
        return queryStatement.toString();
    }

    @Override
    public String queryForFieldNameList(String applicationName, String metricName) {
        StringBuilder queryStatement = setLimit(
                addAndStatement(
                        addWhereStatement(
                                buildBasicQuery(true, "fieldName"),
                                "applicationName", applicationName),
                        "metricName", metricName),
                20);

        return queryStatement.toString();
    }

    @Override
    public String queryTimestampForField(String applicationName, String metricName, String fieldName) {
        StringBuilder queryStatement = setLimit(
                addAndStatement(
                    addAndStatement(
                            addWhereStatement(
                                    buildBasicQuery(true, "timestampInEpoch"),
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
                                        buildBasicQuery(false, "tagName, tagValue"),
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
                                buildBasicQuery(false, "*"),
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


    private StringBuilder buildBasicQuery(boolean distinct, String target) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        sb.append(target);
        sb.append(" FROM systemMetric");
        return sb;
    }

    private StringBuilder addWhereStatement(StringBuilder query, String key, String value) {
        return query.append(" WHERE ").append(key).append("='").append(value).append("'");
    }

    private StringBuilder addAndStatement(StringBuilder query, String key, String value) {
        return query.append(" AND ").append(key).append("='").append(value).append("'");
    }

    private StringBuilder addAndStatement(StringBuilder query, String key, long value) {
        return query.append(" AND ").append(key).append("=").append(value);
    }

    private StringBuilder addOrStatement(StringBuilder query, String key, String value) {
        return query.append(" OR ").append(key).append("='").append(value).append("'");
    }

    private StringBuilder setLimit(StringBuilder query, long limit) {
        return query.append(" LIMIT ").append(limit);
    }

    private StringBuilder addRangeStatement(StringBuilder query, Range range) {

        return query.append(" AND ").append("timestampInEpoch").append(" > ").append(range.getFrom())
                .append(" AND ").append("timestampInEpoch").append(" < ").append(range.getTo());
    }

}
