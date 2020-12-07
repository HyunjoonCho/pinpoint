package com.navercorp.pinpoint.web.metric.dao.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.metric.util.pinot.PinotQueryStatementWriter;
import com.navercorp.pinpoint.web.metric.util.pinot.PinotResultSetProcessor;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PinotDaoTest {
    private PinotQueryStatementWriter pinotQueryStatementWriter = new PinotQueryStatementWriter();
    private PinotResultSetProcessor pinotResultSetProcessor = new PinotResultSetProcessor();
    private SystemMetricDao systemMetricDao = new PinotSystemMetricDao("10.113.84.89:2191", "PinotCluster", pinotQueryStatementWriter, pinotResultSetProcessor);

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
