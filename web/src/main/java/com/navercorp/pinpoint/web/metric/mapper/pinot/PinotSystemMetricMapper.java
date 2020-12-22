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

package com.navercorp.pinpoint.web.metric.mapper.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.FieldBo;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.mapper.SystemMetricMapper;
import org.apache.pinot.client.ResultSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hyunjoon Cho
 */
//@Component
public class PinotSystemMetricMapper extends SystemMetricMapper {

    public List<String> processStringList(ResultSet resultSet) {
        int numRows = resultSet.getRowCount();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            list.add(resultSet.getString(i,0));
        }

        Collections.sort(list);

        return list;
    }

    public List<TagBo> processTagBoList(ResultSet resultSet) {
        int numRows = resultSet.getRowCount();
        List<TagBo> tagBoList = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (TagBo tagBo : parseTagBos(resultSet.getString(i, 0), resultSet.getString(i, 1))) {
                if (!tagBoList.contains(tagBo)) {
                    tagBoList.add(tagBo);
                }
            }
        }

        return tagBoList;
    }

    public List<SystemMetricBo> processSystemMetricBoList(ResultSet resultSet) {
        int numRows = resultSet.getRowCount();
        List<SystemMetricBo> systemMetricBoList = new ArrayList<>();

        boolean isLong = resultSet.getDouble(0, 1) == -1;

        FieldBo fieldBo;
        for (int i = 0; i < numRows; i++) {
            if (isLong) {
                fieldBo = new FieldBo(resultSet.getString(i, 3), resultSet.getLong(i, 2));
            } else {
                fieldBo = new FieldBo(resultSet.getString(i, 3), resultSet.getDouble(i, 1));
            }

            systemMetricBoList.add(
                    new SystemMetricBo(
                            fieldBo,
                            resultSet.getString(i, 4),
                            parseTagBos(resultSet.getString(i, 5), resultSet.getString(i, 6)),
                            resultSet.getLong(i, 7)
                    )
            );
        }

        return systemMetricBoList;
    }

    private List<TagBo> parseTagBos(String tagNames, String tagValues) {
        List<TagBo> tagBos = new ArrayList<>();

        String[] tagName = parseMultiValueFieldList(tagNames);
        String[] tagValue = parseMultiValueFieldList(tagValues);
        // Q. what if there is no tag?
        // A. host is tagged globally by default

        for (int j = 0; j < tagName.length; j++) {
            tagBos.add(new TagBo(tagName[j], tagValue[j]));
            // Q. what it length differs? A. it must not be!
        }

        return tagBos;
    }

}
