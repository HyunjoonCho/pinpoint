package com.navercorp.pinpoint.web.metric.util.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.FieldBo;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import org.apache.pinot.client.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PinotResultSetProcessor {

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
            tagBoList.addAll(parseTagBos(resultSet.getString(i, 0), resultSet.getString(i, 1)));
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

    private String[] parseMultiValueFieldList(String string) {
        return string.substring(1, string.length() - 1).replace("\"", "").split(",");
    }

}
