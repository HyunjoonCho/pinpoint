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

package com.navercorp.pinpoint.collector.metric.serializer.pinot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.navercorp.pinpoint.collector.metric.serializer.SystemMetricSerializer;
import com.navercorp.pinpoint.common.server.metric.bo.FieldBo;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hyunjoon Cho
 */
@Component
public class PinotSystemMetricSerializer implements SystemMetricSerializer {
    private ObjectMapper objectMapper;
    private Map<String, Boolean> fieldLongMap;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PinotSystemMetricSerializer() {
        objectMapper = new ObjectMapper();
        fieldLongMap = new HashMap<>();
    }

    public List<String> serialize(String applicationName, List<SystemMetricBo> systemMetricBos) throws JsonProcessingException {
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
                node.put("fieldValue", fieldBo.getFieldLongValue());
            }else {
                node.put("fieldValue", fieldBo.getFieldDoubleValue());
            }

            fieldLongMap.put(systemMetricBo.getMetricName().concat("_").concat(fieldBo.getFieldName()), fieldBo.isLong());
            if(fieldBo.isLong()) {
                systemMetricStringList.add("L".concat(objectMapper.writeValueAsString(node)));
            } else {
                systemMetricStringList.add(objectMapper.writeValueAsString(node));
            }
        }

        return systemMetricStringList;
    }

    @PreDestroy
    public void saveMetadata() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File("/Users/user/pinpoint/commons-server/SystemMetricMetadata.txt")));
        oos.writeObject(fieldLongMap);
        oos.close();
        logger.info("Wrote metadata to file!");
    }
}
