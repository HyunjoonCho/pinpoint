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
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.metric.vo.SampledSystemMetric;
import com.navercorp.pinpoint.web.metric.vo.chart.SystemMetricChart;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Service
public class SystemMetricService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SystemMetricDao systemMetricDao;
    private final Map<String, Boolean> fieldLongMap;

    public SystemMetricService(SystemMetricDao systemMetricDao) {
        this.systemMetricDao = Objects.requireNonNull(systemMetricDao, "systemMetricDao");
        this.fieldLongMap = loadMetadata();
    }

    private Map<String, Boolean> loadMetadata() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("/Users/user/pinpoint/commons-server/SystemMetricMetadata.txt")));
            Map<String, Boolean> map = (Map<String, Boolean>) ois.readObject();
            logger.info("metadata {}", map.toString());
            return map;
        } catch (Exception e) {
            logger.warn("Failed to load metadata {}", e.getMessage());
        }
        return null;
    }

    public List<String> getMetricNameList(String applicationName) {
        return systemMetricDao.getMetricNameList(Objects.requireNonNull(applicationName, "applicationName"));
    }

    public List<String> getFieldNameList(String applicationName, String metricName) {
        return systemMetricDao.getFieldNameList(
                Objects.requireNonNull(applicationName, "applicationName"),
                Objects.requireNonNull(metricName, "metricName")
        );
    }

    public List<TagBo> getTagBoList(String applicationName, String metricName, String fieldName) {
        return systemMetricDao.getTagBoList(
                Objects.requireNonNull(applicationName, "applicationName"),
                Objects.requireNonNull(metricName, "metricName"),
                Objects.requireNonNull(fieldName, "fieldName"),
                fieldLongMap.get(metricName.concat("_").concat(fieldName))
        );
    }

    public List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<String> tags, Range range){

        return systemMetricDao.getSystemMetricBoList(
                Objects.requireNonNull(applicationName, "applicationName"),
                Objects.requireNonNull(metricName, "metricName"),
                Objects.requireNonNull(fieldName, "fieldName"),
                parseTags(Objects.requireNonNull(tags, "tags")),
                fieldLongMap.get(metricName.concat("_").concat(fieldName)),
                range
        );
    }

    public SystemMetricChart getSystemMetricChart(String applicationName, String metricName, String fieldName, List<String> tags, TimeWindow timeWindow) {
        String chartName = metricName.concat("_").concat(fieldName);
        boolean isLong = fieldLongMap.get(chartName);
        List<SampledSystemMetric> sampledSystemMetrics = systemMetricDao.getSampledSystemMetric(
                Objects.requireNonNull(applicationName, "applicationName"),
                Objects.requireNonNull(metricName, "metricName"),
                Objects.requireNonNull(fieldName, "fieldName"),
                parseTags(Objects.requireNonNull(tags, "tags")),
                isLong,
                timeWindow
        );

        return new SystemMetricChart(timeWindow, chartName, isLong, sampledSystemMetrics);
    }

    public List<TagBo> parseTags(List<String> tags) {
        List<TagBo> tagBoList = new ArrayList<>();
        for (String tag : tags) {
            String[] tagSplit = tag.split(":");
            tagBoList.add(new TagBo(tagSplit[0], tagSplit[1]));
        }
        return tagBoList;
    }
}
