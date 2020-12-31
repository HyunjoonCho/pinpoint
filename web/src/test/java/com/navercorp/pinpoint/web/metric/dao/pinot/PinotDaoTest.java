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

package com.navercorp.pinpoint.web.metric.dao.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.metric.util.pinot.PinotQueryStatementWriter;
import com.navercorp.pinpoint.web.metric.mapper.pinot.PinotSystemMetricMapper;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hyunjoon Cho
 */
public class PinotDaoTest {
    private PinotQueryStatementWriter pinotQueryStatementWriter = new PinotQueryStatementWriter();
    private PinotSystemMetricMapper pinotSystemMetricMapper = new PinotSystemMetricMapper();
    private SystemMetricDao systemMetricDao = new PinotSystemMetricDao(pinotQueryStatementWriter, pinotSystemMetricMapper);
//    private SystemMetricDao systemMetricDao = new PinotSystemMetricDao("IP:PORT", "PinotCluster", pinotQueryStatementWriter, pinotSystemMetricMapper);
// instant fix for controller test > how to make String - zkURL, pinotClusterName - configurable?

    @Test
    public void getMetricNameTest() {
        List<String> metricNameList = systemMetricDao.getMetricNameList("hyunjoon.cho");
        System.out.println(metricNameList);
    }

    @Test
    public void getFieldNameTest() {
        List<String> fieldNameList = systemMetricDao.getFieldNameList("hyunjoon.cho", "cpu");
        System.out.println(fieldNameList);
    }

    @Test
    public void getTagBoListTest() {
        List<TagBo> tagBoList = systemMetricDao.getTagBoList("hyunjoon.cho", "cpu", "usage_idle");
        System.out.println(tagBoList);
    }

    @Test
    public void getSystemMetricBoListTest() {
        systemMetricBoTestTemplate("cpu", "usage_idle", "cpu", "cpu0");
        systemMetricBoTestTemplate("cpu", "usage_idle", "cpu", "cpu2");
        systemMetricBoTestTemplate("system", "uptime", "host", "AL01256785.local");
    }

    private void systemMetricBoTestTemplate(String metricName, String fieldName, String tagName, String tagValue) {
        List<TagBo> tagBos = new ArrayList<>();
        tagBos.add(new TagBo(tagName, tagValue));
        Range range = Range.newRange(1607319340000L, 1607319480000L);
        List<SystemMetricBo> systemMetricBoList = systemMetricDao.getSystemMetricBoList("hyunjoon.cho", metricName, fieldName, tagBos, range);
        for (SystemMetricBo systemMetricBo : systemMetricBoList) {
            System.out.println(systemMetricBo);
        }
    }
}
