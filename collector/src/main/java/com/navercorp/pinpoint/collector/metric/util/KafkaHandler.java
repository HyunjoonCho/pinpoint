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

package com.navercorp.pinpoint.collector.metric.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * @author Hyunjoon Cho
 */
public class KafkaHandler {
    private Properties configs;
    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    may turn to provider or factory if Kafka is required more widely

    public KafkaHandler(String bootstrapServers) {
        // read from config or get as parameter
        // for now, get as argument
        configs = new Properties();
        configs.put("bootstrap.servers", bootstrapServers);
        configs.put("acks", "all");
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        logger.info("HERE!!!!");
        kafkaProducer = new KafkaProducer<>(configs);
//        logger.info("Producer Created!!!");
        topic = "system-metric-topic";
    }

    public void pushData(List<String> systemMetricStringList) {
        logger.info("before time {}", System.currentTimeMillis());
        for (String systemMetric : systemMetricStringList) {
//            logger.info(systemMetric);
            kafkaProducer.send(new ProducerRecord<>(topic, systemMetric));
        }
        kafkaProducer.flush();
        logger.info("after time {}", System.currentTimeMillis());
    }

    public void closeProducer() {
        if (kafkaProducer != null){
            kafkaProducer.close();
            logger.info("Kafka Producer Closed");
        }
    }
}
