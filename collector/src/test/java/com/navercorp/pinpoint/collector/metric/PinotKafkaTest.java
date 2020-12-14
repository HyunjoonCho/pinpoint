package com.navercorp.pinpoint.collector.metric;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.Test;

import java.util.Date;
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
//        for (int i = 0 ; i < 100 ; i++) {
            String systemMetric = "{\"applicationName\":\"hyunjoon\", \"metricName\":\"cpu\", \"fieldName\":\"user_usage\", \"tagName\":[\"host\",\"cpu\", \"currentTime\"], \"tagValue\":[\"cpu1\",\"localhost\", \"" + new Date().toString() + "\"], \"timestampInEpoch\":1571900400000}";
            producer.send(new ProducerRecord<>("system-metric-topic", systemMetric));
//        }
        // skipped fieldValue to see other parts work or not

        producer.flush();
        producer.close();
    }
//
//    @Test
//    public void pinotKafkaStreamsTest() {
//        final String bootstrapServers = "10.113.84.89:19092";
//        final String inputTopic = "system-metric-streams";
//        final String outputTopic = "system-metric-topic";
//
//        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);
//        final StreamsBuilder builder = new StreamsBuilder();
//        KStream<String, String> inputStream = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
//        inputStream.to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
//
//        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
//        streams.cleanUp();
//        streams.start();
//    }
//
//    private Properties getStreamsConfiguration(final String bootstrapServers) {
//        final Properties streamsConfiguration = new Properties();
//        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
//        // against which the application is run.
//        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "system-metric-test");
//        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "system-metric-test-client");
//        // Where to find Kafka broker(s).
//        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        // Specify default (de)serializers for record keys and for record values.
//        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        // Records should be flushed every 10 seconds. This is less than the default
//        // in order to keep this example interactive.
//        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
//        // For illustrative purposes we disable record caches.
//        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
//        return streamsConfiguration;
//    }

}

/*
 */