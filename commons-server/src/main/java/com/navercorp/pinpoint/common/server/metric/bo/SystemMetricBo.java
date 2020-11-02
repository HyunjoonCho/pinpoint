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

package com.navercorp.pinpoint.common.server.metric.bo;

import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
public class SystemMetricBo {
    private final FieldBo fieldBo;
    private final String metricName;
    private final List<TagBo> tagBos;
    private final long timestamp;

    public SystemMetricBo(FieldBo fieldBo, String metricName, List<TagBo> tagBos, long timestamp) {
        this.fieldBo = Objects.requireNonNull(fieldBo, "field");
        this.metricName = Objects.requireNonNull(metricName, "name");
        this.tagBos = Objects.requireNonNull(tagBos, "tags");
        this.timestamp = timestamp;
    }

    public FieldBo getFieldBo() {
        return fieldBo;
    }

    public String getMetricName() {
        return metricName;
    }

    public List<TagBo> getTagBos() {
        return tagBos;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SystemMetric{");
        sb.append("metric=").append(metricName);
        sb.append(", field=").append(fieldBo);
        sb.append(", tags=").append(tagBos);
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();

    }
}
