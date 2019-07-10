package com.kafka.producers;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.Properties;

/**
 * Created by sachin on 10/7/19.
 */
public class KeyedProducerWithCallBack {

    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    static Logger logger = LoggerFactory.getLogger(KeyedProducerWithCallBack.class);

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
            String data=null;
            while ((data=reader.readLine()) !=null) {
                String key =data.split(">>")[0];
                final String immutableMsg = data.split(">>")[1];
                ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic",key, immutableMsg);

                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            logger.error("exception sending request-.{}", exception);
                        } else {
                            logger.info("sending data >>{}"+immutableMsg);
                            logger.info("topic->{}",metadata.topic());
                            logger.info("send time " + new Date(metadata.timestamp()));
                            logger.info("partition->{}  and offset->{}",metadata.partition(),metadata.offset() );
                            logger.info(" msg sending complete ");
                        }
                    }
                });
                 producer.flush();
            }
        } finally {
            producer.close();
        }

    }
}
