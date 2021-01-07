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

package com.navercorp.pinpoint.web.metric.service;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricMetadata;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.metric.vo.SampledSystemMetric;
import com.navercorp.pinpoint.web.metric.vo.chart.SystemMetricChart;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Service
public class SystemMetricService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SystemMetricDao systemMetricDao;
    private final SystemMetricMetadata systemMetricMetadata;

    public SystemMetricService(SystemMetricDao systemMetricDao) {
        this.systemMetricDao = Objects.requireNonNull(systemMetricDao, "systemMetricDao");
        this.systemMetricMetadata = SystemMetricMetadata.getMetadata();
    }

    public List<String> getMetricNameList(String applicationName) {
        Objects.requireNonNull(applicationName, "applicationName");
        return systemMetricDao.getMetricNameList(applicationName);
    }

    public List<String> getFieldNameList(String applicationName, String metricName) {
        Objects.requireNonNull(applicationName, "applicationName");
        Objects.requireNonNull(metricName, "metricName");
        return systemMetricDao.getFieldNameList(applicationName, metricName);
    }

    // TODO: Replace isLong with MetricType / Separate MetricType enum?
    public List<TagBo> getTagBoList(String applicationName, String metricName, String fieldName) {
        Objects.requireNonNull(applicationName, "applicationName");
        Objects.requireNonNull(metricName, "metricName");
        Objects.requireNonNull(fieldName, "fieldName");
        SystemMetricMetadata.MetricType metricType = systemMetricMetadata.get(metricName.concat("_").concat(fieldName));
        return systemMetricDao.getTagBoList(applicationName, metricName, fieldName, false);
    }

    public List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<String> tags, Range range){
        Objects.requireNonNull(applicationName, "applicationName");
        Objects.requireNonNull(metricName, "metricName");
        Objects.requireNonNull(fieldName, "fieldName");
        Objects.requireNonNull(tags, "tags");
        List<TagBo> tagBoList = parseTags(tags);
        SystemMetricMetadata.MetricType metricType = systemMetricMetadata.get(metricName.concat("_").concat(fieldName));
        return systemMetricDao.getSystemMetricBoList(applicationName, metricName, fieldName, tagBoList, false, range);
    }

    public SystemMetricChart getSystemMetricChart(String applicationName, String metricName, String fieldName, List<String> tags, TimeWindow timeWindow) {
        Objects.requireNonNull(applicationName, "applicationName");
        Objects.requireNonNull(metricName, "metricName");
        Objects.requireNonNull(fieldName, "fieldName");
        Objects.requireNonNull(tags, "tags");
        List<TagBo> tagBoList = parseTags(tags);
        String chartName = metricName.concat("_").concat(fieldName);
        SystemMetricMetadata.MetricType metricType = systemMetricMetadata.get(chartName);
        List<SampledSystemMetric> sampledSystemMetrics = systemMetricDao.getSampledSystemMetric(applicationName, metricName, fieldName, tagBoList, false, timeWindow);
        return new SystemMetricChart(timeWindow, chartName, false, sampledSystemMetrics);
    }

    // TODO: Extract to util
    public List<TagBo> parseTags(List<String> tags) {
        List<TagBo> tagBoList = new ArrayList<>();
        for (String tag : tags) {
            String[] tagSplit = tag.split(":");
            tagBoList.add(new TagBo(tagSplit[0], tagSplit[1]));
        }
        return tagBoList;
    }
}
