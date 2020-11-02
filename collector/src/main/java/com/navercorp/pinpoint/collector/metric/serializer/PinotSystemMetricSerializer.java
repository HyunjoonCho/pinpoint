package com.navercorp.pinpoint.collector.metric.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.navercorp.pinpoint.common.server.metric.bo.FieldBo;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PinotSystemMetricSerializer {
    private ObjectMapper objectMapper;

    public PinotSystemMetricSerializer() {
        objectMapper = new ObjectMapper();
    }

    public List<String> serialize(String applicationName, List<SystemMetricBo> systemMetricBos) {
        if (systemMetricBos.isEmpty()) {
            return null;
        }
        List<String> systemMetricStringList = new ArrayList<>();

        for (SystemMetricBo systemMetricBo : systemMetricBos) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("applicationName", applicationName);
            node.put("metricName", systemMetricBo.getMetricName());
            node.put("timestampInEpoch", systemMetricBo.getTimestamp());
            ArrayNode tagName = node.putArray("tagName");
            ArrayNode tagValue = node.putArray("tagValue");
            for (TagBo tagBo : systemMetricBo.getTagBos()) {
                tagName.add(tagBo.getTagName());
                tagValue.add(tagBo.getTagValue());
            }
            FieldBo fieldBo = systemMetricBo.getFieldBo();
            node.put("fieldName", fieldBo.getFieldName());
            if (fieldBo.isLong()) {
                node.put("fieldLongValue", fieldBo.getFieldLongValue());
            }else {
                node.put("fieldDoubleValue", fieldBo.getFieldDoubleValue());
            }
            try {
                systemMetricStringList.add(objectMapper.writeValueAsString(node));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return systemMetricStringList;
    }


}
