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

package com.navercorp.pinpoint.collector.metric.service;

import com.navercorp.pinpoint.collector.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import org.springframework.stereotype.Service;

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

    public void insert(String applicationName, List<SystemMetricBo> systemMetricBos) {
        systemMetricDao.insert(applicationName, systemMetricBos);
    }
}
