package com.navercorp.pinpoint.collector.metric;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class PinotKafkaTest {
    Random random = new Random();

    @Test
    public void kafkaFixedMetricTest() {
        KafkaProducer<String, String> producer = createProducer();

        long time = 0;
        for (int i = 0 ; i < 100000 ; i++) {
            String systemMetric = "{\"applicationName\":\"hyunjoon\", \"tagName\":[\"host\", \"currentTime\"], \"tagValue\":[\"localhost\", \"" + new Date().toString() + "\"], \"timestampInEpoch\":" + time + "}";
            producer.send(new ProducerRecord<>("test-topic", systemMetric));
            time += 1000;
        }
        // skipped fieldValue to see other parts work or not

        producer.flush();
        producer.close();
    }

    @Test
    public void kafkaRandomMetricTest() {

        KafkaProducer<String, String> producer = createProducer();

        long time = 0;
        int num;
        StringBuilder sb;

        for (int i = 0 ; i < 100000 ; i++) {
            sb = new StringBuilder();
            sb.append("{\"applicationName\":\"hyunjoon\", ");
            sb.append("\"metricName\":").append("\"metric").append(getRandomAlphabet()).append("\", ");
            sb.append("\"fieldName\":").append("\"field").append(getRandomAlphabet()).append("\", ");

            sb.append("\"tagName\":[");
            num = random.nextInt(10);
            for (int j = 0; j < num; j++) {
                sb.append("\"name").append(getRandomAlphabet()).append("\", ");
            }
            sb.append("\"currentTime\"], ");

            sb.append("\"tagValue\":[");
            for (int j = 0; j < num; j++) {
                sb.append("\"value").append(getRandomAlphabet()).append("\", ");
            }
            sb.append("\"").append(new Date().toString()).append("\"], ");

            sb.append("\"timestamp\":" + time +"}");

            producer.send(new ProducerRecord<>("random-test-topic", sb.toString()));
            time += 1000;
        }
        // skipped fieldValue to see other parts work or not

        producer.flush();
        producer.close();
    }

    private char getRandomAlphabet() {
        if(random.nextBoolean()){
            return (char) (65 + random.nextInt(26));
        } else {
            return (char) (97 + random.nextInt(26));
        }
    }

    private KafkaProducer<String, String> createProducer() {
        // servers -> localhost to test on local environment
        Properties configs = new Properties();
        configs.put("bootstrap.servers", "10.113.84.140:19092");
        configs.put("acks", "all");
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

//        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);
//        String transcript = "{\"studentID\":207, \"firstName\":\"Hyunjoon\", \"lastName\":\"Cho\", \"gender\":\"Male\", \"subject\":\"Art\", \"score\":3.4, \"timestampInEpoch\":1571900400000}";
//        producer.send(new ProducerRecord<>("transcript-topic", transcript));
        return new KafkaProducer<>(configs);
    }
}
