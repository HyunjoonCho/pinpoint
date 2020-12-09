package com.navercorp.pinpoint.collector.metric;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class PinotKafkaHandler {
    private Properties configs;
    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // may turn to provider or factory if Kafka is required more widely

    public PinotKafkaHandler() {
        // read from config or get as argument
        // for now, use with fixed config
        configs = new Properties();
        configs.put("bootstrap.servers", "10.113.84.89:19092");
        configs.put("acks", "all");
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<>(configs);
        topic = "system-metric-topic";
    }

    public void pushData(List<String> systemMetricStringList) {
        int count = 0;
        final int bufferSize = 5;
        for (String systemMetric : systemMetricStringList) {
            kafkaProducer.send(new ProducerRecord<>(topic, systemMetric));
            count++;
            if (count % bufferSize == 0) {
                kafkaProducer.flush();
            }
//            logger.info("Kafka Handler: {}", systemMetric);
        }
        kafkaProducer.flush();
    }

    public void closeProducer() {
        kafkaProducer.close();
    }
}
