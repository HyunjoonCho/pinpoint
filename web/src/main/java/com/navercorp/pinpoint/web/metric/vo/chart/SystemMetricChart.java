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

package com.navercorp.pinpoint.web.metric.vo.chart;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.server.metric.bo.FieldBo;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.chart.Chart;
import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.chart.TimeSeriesChartBuilder;
import com.navercorp.pinpoint.web.vo.chart.UncollectedPointCreatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Hyunjoon Cho
 */
public class SystemMetricChart {
    private final SystemMetricChartGroup systemMetricChartGroup;

    public SystemMetricChart(TimeWindow timeWindow, List<SystemMetricBo> systemMetricBoList) {
        this.systemMetricChartGroup = new SystemMetricChartGroup(timeWindow, systemMetricBoList);
    }

    public static class SystemMetricChartGroup {

        private final String chartName;

        private final boolean isLong;

        private final TimeWindow timeWindow;

        private final List<List<TagBo>> tagsList;

        private final List<Chart> charts;

        private SystemMetricChartGroup(TimeWindow timeWindow, List<SystemMetricBo> systemMetricBos) {
            this.timeWindow = Objects.requireNonNull(timeWindow, "timeWindow");

            SystemMetricBo systemMetricBo = systemMetricBos.get(0);
            this.chartName = systemMetricBo.getMetricName().concat("_").concat(systemMetricBo.getFieldBo().getFieldName());
            this.isLong = systemMetricBo.getFieldBo().isLong();

            Map<List<TagBo>, List<SystemMetricBo>> taggedSystemMetrics = systemMetricBos.stream().collect(Collectors.groupingBy(bo -> bo.getTagBos()));
            this.tagsList = processTagList(taggedSystemMetrics);
            this.charts = processChartList(taggedSystemMetrics, isLong);
        }

        private List<List<TagBo>> processTagList(Map<List<TagBo>, List<SystemMetricBo>> taggedSystemMetrics) {
            ImmutableList.Builder<List<TagBo>> builder = ImmutableList.builder();
            for (Map.Entry<List<TagBo>, List<SystemMetricBo>> entry : taggedSystemMetrics.entrySet()) {
                builder.add(entry.getKey());
            }
            return builder.build();
        }

        private List<Chart> processChartList(Map<List<TagBo>, List<SystemMetricBo>> taggedSystemMetrics, boolean isLong) {
            ImmutableList.Builder<Chart> builder = ImmutableList.builder();
            if (isLong) {
                for (Map.Entry<List<TagBo>, List<SystemMetricBo>> entry : taggedSystemMetrics.entrySet()) {
                    builder.add(newLongChart(getFieldBosFromSystemMetricBos(entry.getValue())));
                }
            } else {
                for (Map.Entry<List<TagBo>, List<SystemMetricBo>> entry : taggedSystemMetrics.entrySet()) {
                    builder.add(newDoubleChart(getFieldBosFromSystemMetricBos(entry.getValue())));
                }
            }

            return builder.build();
        }

        private List<FieldBo> getFieldBosFromSystemMetricBos(List<SystemMetricBo> systemMetricBos) {
            return systemMetricBos.stream().map(SystemMetricBo::getFieldBo).collect(Collectors.toList());
        }

        private Chart<SystemMetricPoint<Long>> newLongChart(List<FieldBo> systemMetricBos) {
            Point.UncollectedPointCreator<SystemMetricPoint<Long>> uncollectedPointCreator = xVal -> new SystemMetricPoint<>(xVal, -1L);

            TimeSeriesChartBuilder<SystemMetricPoint<Long>> builder = new TimeSeriesChartBuilder<>(this.timeWindow, uncollectedPointCreator);
//            return builder.build(systemMetricBos, FieldBo::getFieldLongValue);
            return null;
        }

        private Chart<SystemMetricPoint<Double>> newDoubleChart(List<FieldBo> systemMetricBos) {
            return null;
        }
    }
}
