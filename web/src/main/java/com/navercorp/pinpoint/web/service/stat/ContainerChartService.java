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

package com.navercorp.pinpoint.web.service.stat;

import com.navercorp.pinpoint.web.dao.stat.SampledContainerDao;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.stat.SampledContainer;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChart;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.ContainerChart;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Service
public class ContainerChartService implements AgentStatChartService{

    private final SampledContainerDao sampledContainerDao;

    public ContainerChartService(@Qualifier("sampledContainerDaoFactory") SampledContainerDao sampledContainerDao) {
        this.sampledContainerDao = Objects.requireNonNull(sampledContainerDao, "sampledContainerDao");
    }

    @Override
    public StatChart selectAgentChart(String agentId, TimeWindow timeWindow) {
        Objects.requireNonNull(agentId, "agentId");
        Objects.requireNonNull(timeWindow, "timeWindow");

        List<SampledContainer> sampledContainers = this.sampledContainerDao.getSampledAgentStatList(agentId, timeWindow);
        return new ContainerChart(timeWindow, sampledContainers);
    }

    @Override
    public List<StatChart> selectAgentChartList(String agentId, TimeWindow timeWindow) {
        StatChart agentStatChart = selectAgentChart(agentId, timeWindow);

        List<StatChart> result = new ArrayList<>(1);
        result.add(agentStatChart);

        return result;
    }
}
