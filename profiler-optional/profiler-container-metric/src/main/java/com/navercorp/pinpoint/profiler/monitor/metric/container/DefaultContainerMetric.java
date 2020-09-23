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

package com.navercorp.pinpoint.profiler.monitor.metric.container;

import jdk.internal.platform.Container;
import jdk.internal.platform.Metrics;

/**
 * @author Hyunjoon Cho
 */
public class DefaultContainerMetric implements ContainerMetric{

    private final Metrics metrics;  //replace with MXBean?
    private long prevUserCpuUsage;
    private long prevSystemCpuUsage;

    public DefaultContainerMetric() {
        metrics = Container.metrics();
        prevUserCpuUsage = metrics.getCpuUserUsage();
        prevSystemCpuUsage = metrics.getCpuSystemUsage();
    }

    @Override
    public ContainerMetricSnapshot getSnapshot() {
        final long userCpuUsage = metrics.getCpuUserUsage() - prevUserCpuUsage;
        prevUserCpuUsage = metrics.getCpuUserUsage();
        final long systemCpuUsage = metrics.getCpuSystemUsage() - prevSystemCpuUsage;
        prevSystemCpuUsage = metrics.getCpuSystemUsage();
        //buggy at first collection?
        //divide by total time? what if multi-core?
        final long memoryUsage = metrics.getMemoryUsage();

        return new ContainerMetricSnapshot(userCpuUsage, systemCpuUsage, memoryUsage);
    }

    @Override
    public String toString(){
        return "DefaultContainerMetric";
    }
}
