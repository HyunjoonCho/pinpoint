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
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Service
public class SystemMetricService {
    private final SystemMetricDao systemMetricDao;

    public SystemMetricService(SystemMetricDao systemMetricDao) {
        this.systemMetricDao = Objects.requireNonNull(systemMetricDao, "systemMetricDao");
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
                Objects.requireNonNull(fieldName, "fieldName")
        );
    }

    public List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<String> tags, long from, long to){
        return systemMetricDao.getSystemMetricBoList(
                Objects.requireNonNull(applicationName, "applicationName"),
                Objects.requireNonNull(metricName, "metricName"),
                Objects.requireNonNull(fieldName, "fieldName"),
                parseTags(Objects.requireNonNull(tags, "tags")),
                Range.newRange(from, to)
        );
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
