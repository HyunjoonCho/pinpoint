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

package com.navercorp.pinpoint.web.mapper.stat.sampling.sampler;

import com.navercorp.pinpoint.common.server.bo.stat.ContainerBo;
import com.navercorp.pinpoint.web.vo.stat.SampledContainer;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSampler;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSamplers;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;

/**
 * @author Hyunjoon Cho
 */
@Component
public class ContainerSampler implements AgentStatSampler<ContainerBo, SampledContainer> {

    private static final DownSampler<Long> LONG_DOWN_SAMPLER = DownSamplers.getLongDownSampler(SampledContainer.UNCOLLECTED_VALUE);

    @Override
    public SampledContainer sampleDataPoints(int index, long timestamp, List<ContainerBo> dataPoints, ContainerBo previousDataPoint) {
        final AgentStatPoint<Long> userCpuUsage = newAgentStatPoint(timestamp, dataPoints, ContainerBo::getUserCpuUsage);
        final AgentStatPoint<Long> systemCpuUsage = newAgentStatPoint(timestamp, dataPoints, ContainerBo::getSystemCpuUsage);
        final AgentStatPoint<Long> memoryUsage = newAgentStatPoint(timestamp, dataPoints, ContainerBo::getMemoryUsage);

        SampledContainer sampledContainer = new SampledContainer(userCpuUsage, systemCpuUsage, memoryUsage);
        return sampledContainer;
    }

    private AgentStatPoint<Long> newAgentStatPoint(long timestamp, List<ContainerBo> dataPoints, ToLongFunction<ContainerBo> filter) {
        List<Long> containers = filter(dataPoints, filter);
        return createPoint(timestamp, containers);
    }

    private List<Long> filter(List<ContainerBo> dataPoints, ToLongFunction<ContainerBo> filter) {
        final List<Long> result = new ArrayList<>(dataPoints.size());
        for (ContainerBo containerBo : dataPoints) {
            final long apply = filter.applyAsLong(containerBo);
            if (apply != ContainerBo.UNCOLLECTED_VALUE) {
                result.add(apply);
            }
        }
        return result;
    }

    private AgentStatPoint<Long> createPoint(long timestamp, List<Long> values) {
        if (values.isEmpty()) {
            return SampledContainer.UNCOLLECTED_POINT_CREATOR.createUnCollectedPoint(timestamp);
        }

        return new AgentStatPoint<>(
                timestamp,
                LONG_DOWN_SAMPLER.sampleMin(values),
                LONG_DOWN_SAMPLER.sampleMax(values),
                LONG_DOWN_SAMPLER.sampleAvg(values),
                LONG_DOWN_SAMPLER.sampleSum(values));

    }
}
