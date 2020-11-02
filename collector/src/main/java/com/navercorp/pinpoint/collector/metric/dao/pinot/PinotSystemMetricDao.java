package com.navercorp.pinpoint.collector.metric.dao.pinot;

import com.navercorp.pinpoint.collector.metric.PinotKafkaHandler;
import com.navercorp.pinpoint.collector.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.collector.metric.serializer.PinotSystemMetricSerializer;
import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class PinotSystemMetricDao implements SystemMetricDao {
    private final PinotSystemMetricSerializer pinotSystemMetricSerializer;
    private final PinotKafkaHandler pinotKafkaHandler;

    public PinotSystemMetricDao(PinotSystemMetricSerializer pinotSystemMetricSerializer, PinotKafkaHandler pinotKafkaHandler) {
        this.pinotSystemMetricSerializer = Objects.requireNonNull(pinotSystemMetricSerializer, "pinotSystemMetricSerializer");
        this.pinotKafkaHandler = Objects.requireNonNull(pinotKafkaHandler, "pinotKafkaHandler");
    }

    @Override
    public void insert(String applicationName, List<SystemMetricBo> systemMetricBos) {
        List<String> systemMetricStringList = pinotSystemMetricSerializer.serialize(applicationName, systemMetricBos);
        pinotKafkaHandler.pushData(systemMetricStringList);
    }
}
