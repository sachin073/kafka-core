package com.kafka.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.*;
import java.util.Properties;

/**
 * Created by sachin on 10/7/19.
 */
public class BasicProducer {

    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws Exception {
        reader = new BufferedReader(new FileReader(new File("src/input.in")));

        //load args for producer
        Properties kafkaProducerConfig = new Properties();
        kafkaProducerConfig.load(new InputStreamReader(new FileInputStream(new File("src/producers.properties"))));
        System.out.println(StringSerializer.class.getName());
        // create kafka producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(kafkaProducerConfig);


        try {
            //send record/data to kafka
            ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","Hello world!");

            producer.send(record);
           // producer.flush();
        } finally {
            producer.close();
        }

    }
}
