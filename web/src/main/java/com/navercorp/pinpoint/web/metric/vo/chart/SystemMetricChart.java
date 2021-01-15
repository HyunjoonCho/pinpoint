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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.web.metric.view.SystemMetricChartSerializer;
import com.navercorp.pinpoint.web.metric.vo.SampledSystemMetric;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.chart.Chart;
import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.chart.TimeSeriesChartBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Hyunjoon Cho
 */
@JsonSerialize(using = SystemMetricChartSerializer.class)
public class SystemMetricChart {
    private final SystemMetricChartGroup systemMetricChartGroup;

    public SystemMetricChart(TimeWindow timeWindow, String chartName, boolean isLong, List<SampledSystemMetric> sampledSystemMetrics) {
        this.systemMetricChartGroup = new SystemMetricChartGroup(timeWindow, chartName, isLong, sampledSystemMetrics);
    }

    public SystemMetricChartGroup getSystemMetricChartGroup() {
        return systemMetricChartGroup;
    }

    public static class SystemMetricChartGroup {

        private final String chartName;

        private final boolean isLong;

        private final TimeWindow timeWindow;

        private final List<String> tagsList;

        private final List<Chart<? extends Point>> charts;

        private SystemMetricChartGroup(TimeWindow timeWindow, String chartName, boolean isLong, List<SampledSystemMetric> sampledSystemMetrics) {
            this.timeWindow = Objects.requireNonNull(timeWindow, "timeWindow");
            this.chartName = Objects.requireNonNull(chartName, "chartName");
            this.isLong = isLong;

            Map<String, List<SampledSystemMetric>> taggedSystemMetrics = sampledSystemMetrics.stream().collect(Collectors.groupingBy(metric -> metric.getTags()));
            this.tagsList = processTagList(taggedSystemMetrics);
            this.charts = processChartList(taggedSystemMetrics, isLong);
        }

        private List<String> processTagList(Map<String, List<SampledSystemMetric>> taggedSystemMetrics) {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            for (Map.Entry<String, List<SampledSystemMetric>> entry : taggedSystemMetrics.entrySet()) {
                builder.add(entry.getKey());
            }
            return builder.build();
        }

        private List<Chart<? extends Point>> processChartList(Map<String, List<SampledSystemMetric>> taggedSystemMetrics, boolean isLong) {
            ImmutableList.Builder<Chart<? extends Point>> builder = ImmutableList.builder();
            if (isLong) {
                for (Map.Entry<String, List<SampledSystemMetric>> entry : taggedSystemMetrics.entrySet()) {
//                    builder.add(newLongChart(entry.getValue(), SampledSystemMetric::getLongPoint));
                }
            } else {
                for (Map.Entry<String, List<SampledSystemMetric>> entry : taggedSystemMetrics.entrySet()) {
//                    builder.add(newDoubleChart(entry.getValue(), SampledSystemMetric::getDoublePoint));
                }
            }

            return builder.build();
        }

        private Chart<SystemMetricPoint<Long>> newLongChart(List<SampledSystemMetric> sampledSystemMetrics, Function<SampledSystemMetric, SystemMetricPoint<Long>> function) {
            TimeSeriesChartBuilder<SystemMetricPoint<Long>> builder = new TimeSeriesChartBuilder<>(this.timeWindow, SampledSystemMetric.UNCOLLECTED_LONG_POINT_CREATOR);
            return builder.build(sampledSystemMetrics, function);
        }

        private Chart<SystemMetricPoint<Double>> newDoubleChart(List<SampledSystemMetric> sampledSystemMetrics, Function<SampledSystemMetric, SystemMetricPoint<Double>> function) {
            TimeSeriesChartBuilder<SystemMetricPoint<Double>> builder = new TimeSeriesChartBuilder<>(this.timeWindow, SampledSystemMetric.UNCOLLECTED_DOUBLE_POINT_CREATOR);
            return builder.build(sampledSystemMetrics, function);
        }

        public String getChartName() {
            return chartName;
        }

        public boolean isLong() {
            return isLong;
        }

        public TimeWindow getTimeWindow() {
            return timeWindow;
        }

        public List<String> getTagsList() {
            return tagsList;
        }

        public List<Chart<? extends Point>> getCharts() {
            return charts;
        }
    }
}
