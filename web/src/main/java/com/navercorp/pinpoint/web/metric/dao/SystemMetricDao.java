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

package com.navercorp.pinpoint.web.metric.dao;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.vo.SampledSystemMetric;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;

/**
 * @author Hyunjoon Cho
 */
public interface SystemMetricDao {
    List<String> getMetricNameList(String applicationName);
    List<String> getFieldNameList(String applicationName, String metricName);
    List<TagBo> getTagBoList(String applicationName, String metricName, String fieldName, boolean isLong);
    List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tags, boolean isLong, Range range);
    List<SampledSystemMetric> getSampledSystemMetric(String applicationName, String metricName, String fieldName, List<TagBo> tags, boolean isLong, TimeWindow timeWindow);
}
