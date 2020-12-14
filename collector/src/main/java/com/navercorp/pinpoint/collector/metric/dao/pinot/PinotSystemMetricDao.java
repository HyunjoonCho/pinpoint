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

package com.navercorp.pinpoint.collector.metric.dao.pinot;

import com.navercorp.pinpoint.collector.metric.PinotKafkaHandler;
import com.navercorp.pinpoint.collector.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.collector.metric.serializer.PinotSystemMetricSerializer;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Repository
public class PinotSystemMetricDao implements SystemMetricDao {
    private final PinotSystemMetricSerializer pinotSystemMetricSerializer;
    private final PinotKafkaHandler pinotKafkaHandler;

    public PinotSystemMetricDao(PinotSystemMetricSerializer pinotSystemMetricSerializer, PinotKafkaHandler pinotKafkaHandler) {
        this.pinotSystemMetricSerializer = Objects.requireNonNull(pinotSystemMetricSerializer, "pinotSystemMetricSerializer");
        this.pinotKafkaHandler = Objects.requireNonNull(pinotKafkaHandler, "pinotKafkaHandler");
    }

    @Override
    public void insert(String applicationName, List<SystemMetricBo> systemMetricBos) {
        List<String> systemMetricStringList = pinotSystemMetricSerializer.serialize(applicationName, systemMetricBos);
        pinotKafkaHandler.pushData(systemMetricStringList);
    }
}
