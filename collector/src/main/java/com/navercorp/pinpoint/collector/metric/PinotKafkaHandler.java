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

package com.navercorp.pinpoint.collector.metric;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;

/**
 * @author Hyunjoon Cho
 */
@Component
public class PinotKafkaHandler {
    private Properties configs;
    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;

    private final static String PINOT_KAFKA_SERVER = "10.113.84.89:19092";
    private final static String DRUID_KAFKA_SERVER = "10.113.84.140:19092";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    may turn to provider or factory if Kafka is required more widely

    public PinotKafkaHandler() {
        // read from config or get as argument
        // for now, use with fixed config
        configs = new Properties();
        configs.put("bootstrap.servers", DRUID_KAFKA_SERVER);
        configs.put("acks", "all");
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        logger.info("HERE!!!!");
        kafkaProducer = new KafkaProducer<>(configs);
//        logger.info("Producer Created!!!");
        topic = "system-metric-topic";
    }

    public void pushData(List<String> systemMetricStringList) {
//        int count = 0;
//        final int bufferSize = 5;
        logger.info("before time {}", System.currentTimeMillis());
        for (String systemMetric : systemMetricStringList) {
            kafkaProducer.send(new ProducerRecord<>(topic, systemMetric));
//            count++;
//            if (count % bufferSize == 0) {
//                kafkaProducer.flush();
//            }
//            logger.info("Kafka Handler: {}", systemMetric);
        }
        kafkaProducer.flush();
        logger.info("after time {}", System.currentTimeMillis());
    }

    public void closeProducer() {
        if (kafkaProducer != null){
            kafkaProducer.close();
        }
    }

    @PreDestroy
    public void destroy() {
        closeProducer();
    }
}
