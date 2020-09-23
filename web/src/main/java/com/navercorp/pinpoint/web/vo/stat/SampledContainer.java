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

package com.navercorp.pinpoint.web.vo.stat;

import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;

import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
public class SampledContainer implements SampledAgentStatDataPoint {

    public static final Long UNCOLLECTED_VALUE = -1L;
    public static final Point.UncollectedPointCreator<AgentStatPoint<Long>> UNCOLLECTED_POINT_CREATOR = new Point.UncollectedPointCreator<AgentStatPoint<Long>>() {
        @Override
        public AgentStatPoint<Long> createUnCollectedPoint(long xVal) {
            return new AgentStatPoint<>(xVal, UNCOLLECTED_VALUE);
        }
    };

    private final AgentStatPoint<Long> userCpuUsage;
    private final AgentStatPoint<Long> systemCpuUsage;
    private final AgentStatPoint<Long> memoryUsage;


    public SampledContainer(AgentStatPoint<Long> userCpuUsage, AgentStatPoint<Long> systemCpuUsage, AgentStatPoint<Long> memoryUsage) {
        this.userCpuUsage = Objects.requireNonNull(userCpuUsage, "userCpuUsage");
        this.systemCpuUsage = Objects.requireNonNull(systemCpuUsage, "systemCpuUsage");
        this.memoryUsage = Objects.requireNonNull(memoryUsage, "memoryUsage");
    }

    public AgentStatPoint<Long> getUserCpuUsage() {
        return userCpuUsage;
    }

    public AgentStatPoint<Long> getSystemCpuUsage() {
        return systemCpuUsage;
    }

    public AgentStatPoint<Long> getMemoryUsage() {
        return memoryUsage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampledContainer{");
        sb.append("userCpuUsage=").append(userCpuUsage);
        sb.append("systemCpuUsage=").append(systemCpuUsage);
        sb.append("memoryUsage=").append(memoryUsage);
        sb.append('}');
        return sb.toString();
    }
}
