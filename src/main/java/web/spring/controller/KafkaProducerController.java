package web.spring.controller;

import com.twitter.hbc.core.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import web.kafka.topic.KafkaTopicConfig;
import web.spring.http.TwitterFeedClient;

import java.sql.Time;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@RestController
public class KafkaProducerController {


    static Logger logger = LoggerFactory.getLogger(KafkaProducerController.class);

    @Autowired
    KafkaTemplate<String,String> template;

    @Autowired
    TwitterFeedClient client;

    @GetMapping("/read/{count}")
    public void readFeed(@PathVariable int count){
        Client twitterFeedClient = client.getTwitterClient();
        twitterFeedClient.connect();

        //safe, due to thread-local
        BlockingQueue<String> queue= TwitterFeedClient.getMsgQ().get();

        try {
            String msg="";
            while (count>0){
                msg=queue.poll(5000, TimeUnit.MILLISECONDS);
                if (msg!=null){
                    logger.info("msg ->{}",msg);
                    template.send(KafkaTopicConfig.twitterTopic,msg);
                }

                logger.info("reading count ->{}",count );
                count--;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            twitterFeedClient.stop();
        }

    }

    @PostMapping("/customTopic/{msg}")
    public String putMsg(@PathVariable String msg){

        template.send(KafkaTopicConfig.testTopic,msg);

        return msg+" send successfully to topic "+KafkaTopicConfig.testTopic;
    }


}
