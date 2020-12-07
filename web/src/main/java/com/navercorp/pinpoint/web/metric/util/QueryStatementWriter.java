package com.navercorp.pinpoint.web.metric.util;

import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;

public interface QueryStatementWriter {
    public String queryForMetricNameList(String applicationName);
    public String queryForFieldNameList(String applicationName, String metricName);
    public String queryTimestampForField(String applicationName, String metricName, String fieldName);
    public String queryForTagBoList(String applicationName, String metricName, String fieldName, long timestamp);
    public String queryForSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tagBos, Range range);
}
