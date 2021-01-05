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

package com.navercorp.pinpoint.web.metric.dao.druid;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.mapper.druid.DruidSystemMetricMapper;
import com.navercorp.pinpoint.web.metric.util.druid.DruidQueryStatementWriter;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hyunjoon Cho
 */
public class DruidDaoTest {
    private final DruidQueryStatementWriter queryStatementWriter = new DruidQueryStatementWriter();
    private final DruidSystemMetricDao dao = new DruidSystemMetricDao(queryStatementWriter, new DruidSystemMetricMapper());

    @Test
    public void queryStatementWriterTest() {
        System.out.println(queryStatementWriter.queryForMetricNameList("hyunjoon.cho", true));
        System.out.println(queryStatementWriter.queryForFieldNameList("hyunjoon.cho", "cpu", true));
        System.out.println(queryStatementWriter.queryForTagBoList("hyunjoon.cho", "cpu", "usage_user", true, 0));
        List<TagBo> tagBoList = new ArrayList<>();
        tagBoList.add(new TagBo("cpu", "cpu0"));
        tagBoList.add(new TagBo("host", "AL01256785.local"));
        System.out.println(queryStatementWriter.queryForSystemMetricBoList("hyunjoon.cho", "cpu", "usage_user", tagBoList, true, Range.newRange(1608711230000L, 1608711290000L)));
    }

    @Test
    public void daoStringListTest() {
        System.out.println(dao.getMetricNameList("hyunjoon.cho"));
        System.out.println(dao.getFieldNameList("hyunjoon.cho", "processes"));
    }

    @Test
    public void daoTagBoListTest() {
        System.out.println(dao.getTagBoList("hyunjoon.cho", "cpu", "usage_user", false));
    }

    @Test
    public void daoDoubleSystemMetricBoListTest() {
        // multiple tags & field double value

        List<TagBo> tagBoList = new ArrayList<>();
        tagBoList.add(new TagBo("cpu", "cpu0"));
        tagBoList.add(new TagBo("host", "AL01256785.local"));

        List<SystemMetricBo> systemMetricBoList = dao.getSystemMetricBoList("hyunjoon.cho", "cpu", "usage_user", tagBoList, true, Range.newRange(1608711230000L, 1608711290000L));
        Assert.assertEquals(7, systemMetricBoList.size());
        Assert.assertEquals(systemMetricBoList.get(0).getTagBos().size(), 2);
        Assert.assertFalse(systemMetricBoList.get(0).getFieldBo().isLong());
        System.out.println(systemMetricBoList);
    }

    @Test
    public void daoLongSystemMetricBoListTest() {
        // single tag & field long value

        List<TagBo> tagBoList = new ArrayList<>();
        tagBoList.add(new TagBo("host", "AL01256785.local"));

        List<SystemMetricBo> systemMetricBoList = dao.getSystemMetricBoList("hyunjoon.cho", "mem", "available", tagBoList, true, Range.newRange(1608711230000L, 1608711290000L));
        Assert.assertEquals(7, systemMetricBoList.size());
        Assert.assertEquals(systemMetricBoList.get(0).getTagBos().size(), 1);
        Assert.assertTrue(systemMetricBoList.get(0).getFieldBo().isLong());
        System.out.println(systemMetricBoList);
    }
}
