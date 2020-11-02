package com.navercorp.pinpoint.collector.metric;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.Properties;

public class PinotKafkaTest {
    @Test
    public void pinotKafkaTest() {
        // servers -> localhost to test on local environment
        Properties configs = new Properties();
        configs.put("bootstrap.servers", "10.113.84.89:19092");
        configs.put("acks", "all");
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

//        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);
//        String transcript = "{\"studentID\":207, \"firstName\":\"Hyunjoon\", \"lastName\":\"Cho\", \"gender\":\"Male\", \"subject\":\"Art\", \"score\":3.4, \"timestampInEpoch\":1571900400000}";
//        producer.send(new ProducerRecord<>("transcript-topic", transcript));
        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);
        String systemMetric = "{\"applicationName\":\"hyunjoon\", \"metricName\":\"cpu\", \"fieldName\":\"user_usage\", \"tagName\":[\"host\",\"cpu\"], \"tagValue\":[\"cpu1\",\"localhost\"], \"timestampInEpoch\":1571900400000}";
        // skipped fieldValue to see other parts work or not
        producer.send(new ProducerRecord<>("system-metric-topic", systemMetric));

        producer.flush();
        producer.close();
    }
}

/*
 */