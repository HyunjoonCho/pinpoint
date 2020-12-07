package com.navercorp.pinpoint.web.metric.dao;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;

public interface SystemMetricDao {
    public List<String> getMetricNameList(String applicationName);
    public List<String> getFieldNameList(String applicationName, String metricName);
    public List<TagBo> getTagBoList(String applicationName, String metricName, String fieldName);
    public List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tags, Range range);
}
