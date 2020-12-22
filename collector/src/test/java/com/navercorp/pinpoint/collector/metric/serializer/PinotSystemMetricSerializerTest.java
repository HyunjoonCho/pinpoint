package com.navercorp.pinpoint.collector.metric.serializer;

import com.navercorp.pinpoint.collector.metric.serializer.pinot.PinotSystemMetricSerializer;
import com.navercorp.pinpoint.common.server.metric.bo.FieldBo;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PinotSystemMetricSerializerTest {
    @Test
    public void serializerTest() {
        List<SystemMetricBo> systemMetricBos = new ArrayList<>();
        FieldBo fieldBo = createFieldBo();
        systemMetricBos.add(new SystemMetricBo(fieldBo, "cpu", createTagBoList(0), 0L));
        systemMetricBos.add(new SystemMetricBo(fieldBo, "cpu", createTagBoList(1), 0L));

        PinotSystemMetricSerializer serializer = new PinotSystemMetricSerializer();
        List<String> serializedMetric = serializer.serialize("hyunjoon", systemMetricBos);
        for (String metric : serializedMetric) {
            System.out.println(metric);
        }
    }

    public FieldBo createFieldBo() {
        return new FieldBo("usage_user", 3.31F);
    }

    public List<TagBo> createTagBoList(int num) {
        List<TagBo> tagBos = new ArrayList<>();
        tagBos.add(new TagBo("cpu", "cpu" + num));
        tagBos.add(new TagBo("host", "localhost"));

        return tagBos;
    }
}
