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

package com.navercorp.pinpoint.web.metric.controller;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.service.SystemMetricService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Controller
public class SystemMetricController {
    private final SystemMetricService systemMetricService;

    public SystemMetricController(SystemMetricService systemMetricService) {
        this.systemMetricService = Objects.requireNonNull(systemMetricService, "systemMetricService");
    }

    @RequestMapping(value = "/systemMetric/metricNameList")
    @ResponseBody
    public List<String> getMetricNameList(@RequestParam(value = "applicationName") String applicationName) {
        return systemMetricService.getMetricNameList(applicationName);
    }

    @RequestMapping(value = "/systemMetric/fieldNameList")
    @ResponseBody
    public List<String> getFieldNameList(
            @RequestParam(value = "applicationName") String applicationName,
            @RequestParam(value = "metricName") String metricName) {
        return systemMetricService.getFieldNameList(applicationName, metricName);
    }

    @RequestMapping(value = "/systemMetric/tagBoList")
    @ResponseBody
    public List<TagBo> getTagBoList(
            @RequestParam(value = "applicationName") String applicationName,
            @RequestParam(value = "metricName") String metricName,
            @RequestParam(value = "fieldName") String fieldName){
        return systemMetricService.getTagBoList(applicationName, metricName, fieldName);
    }

    @RequestMapping(value = "/systemMetric/systemMetricBoList")
    @ResponseBody
    public List<SystemMetricBo> getSystemMetricBoList(
            @RequestParam(value = "applicationName") String applicationName,
            @RequestParam(value = "metricName") String metricName,
            @RequestParam(value = "fieldName") String fieldName,
            @RequestParam(value = "tags") List<String> tags,
            @RequestParam(value = "from") long from,
            @RequestParam(value = "to") long to){
        return systemMetricService.getSystemMetricBoList(applicationName, metricName, fieldName, tags, from, to);
    }

/*
    @RequestMapping(value = "/systemMetric/systemMetricChart")
    @ResponseBody
    public SystemMetricChart getSystemMetricChart(
            @RequestParam(value = "applicationName") String applicationName,
            @RequestParam(value = "metricName") String metricName,
            @RequestParam(value = "fieldName") String fieldName,
            @RequestParam(value = "tags") List<String> tags){

    }
*/
}
