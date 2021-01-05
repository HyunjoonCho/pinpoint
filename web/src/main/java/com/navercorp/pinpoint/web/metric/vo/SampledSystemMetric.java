/*
 * Copyright 2021 NAVER Corp.
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

package com.navercorp.pinpoint.web.metric.vo;

import com.navercorp.pinpoint.web.metric.vo.chart.SystemMetricPoint;
import com.navercorp.pinpoint.web.vo.chart.Point;

import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
public class SampledSystemMetric {
    public static final Long UNCOLLECTED_LONG = -1L;
    public static final Double UNCOLLECTED_DOUBLE = -1D;
    public static final Point.UncollectedPointCreator<SystemMetricPoint<Long>> UNCOLLECTED_LONG_POINT_CREATOR = new Point.UncollectedPointCreator<SystemMetricPoint<Long>>() {
        @Override
        public SystemMetricPoint<Long> createUnCollectedPoint(long xVal) {
            return new SystemMetricPoint<>(xVal, UNCOLLECTED_LONG);
        }
    };
    public static final Point.UncollectedPointCreator<SystemMetricPoint<Double>> UNCOLLECTED_DOUBLE_POINT_CREATOR = new Point.UncollectedPointCreator<SystemMetricPoint<Double>>() {
        @Override
        public SystemMetricPoint<Double> createUnCollectedPoint(long xVal) {
            return new SystemMetricPoint<>(xVal, UNCOLLECTED_DOUBLE);
        }
    };

    private final SystemMetricPoint systemMetricPoint;
    private final String tags;

    public SampledSystemMetric (SystemMetricPoint systemMetricPoint, String tags) {
        this.tags = Objects.requireNonNull(tags, "tags");
        this.systemMetricPoint = Objects.requireNonNull(systemMetricPoint, "systemMetricPoint");
    }

    public SystemMetricPoint<Double> getDoublePoint() {
        return (SystemMetricPoint<Double>) systemMetricPoint;
    }

    public SystemMetricPoint<Long> getLongPoint() {
        return (SystemMetricPoint<Long>) systemMetricPoint;
    }

    public String getTags() {
        return tags;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampledSystemMetric{");
        sb.append("systemMetricPoint=").append(systemMetricPoint);
        sb.append(", tags=").append(tags);
        sb.append('}');
        return sb.toString();
    }
}
