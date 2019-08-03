package web.spring.http;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class TwitterFeedClient implements DisposableBean {

    @Value(value = "${twitter.consumerKey}")
    final static String consumerkey =null;

    @Value(value = "${twitter.consumerSecret}")
    final static String consumerSecret =null;

    @Value(value = "${twitter.token}")
    final static String token =null;

    @Value(value = "${twitter.secret}")
    final static String secret=null ;

    // These secrets should be read from a config file
   static Authentication hosebirdAuth =
            new OAuth1(consumerkey, consumerSecret, token, secret);
   static Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
   static StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

    static ThreadLocal<BlockingQueue<String>> msgQ = new ThreadLocal<BlockingQueue<String>>(){
        @Override
        protected BlockingQueue<String> initialValue() {
            return new LinkedBlockingQueue<>();
        }
    };

    Client twitterClient =null;

    public static ThreadLocal<BlockingQueue<String>> getMsgQ() {
        return msgQ;
    }

    public static void setMsgQ(ThreadLocal<BlockingQueue<String>> msgQ) {
        TwitterFeedClient.msgQ = msgQ;
    }

    public Client getTwitterClient() {
        return twitterClient;
    }

    public void setTwitterClient(Client twitterClient) {
        this.twitterClient = twitterClient;
    }

    TwitterFeedClient(){}

    @Bean
    @Primary
    @Scope(value = "prototype", proxyMode= ScopedProxyMode.TARGET_CLASS)
    public TwitterFeedClient getInstance(){
        return new TwitterFeedClient();
    }

    @PostConstruct
    public void twitterFeedClient( ){
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */

        // Optional: set up some followings and track terms
        List<Long> followings = Lists.newArrayList(1234L, 566788L);
        List<String> terms = Lists.newArrayList("Bitcoin", "usa","india");

        hosebirdEndpoint.followings(followings);
        hosebirdEndpoint.trackTerms(terms);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-"+Thread.currentThread().getName())                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQ.get()))
                .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

        twitterClient =builder.build();
    }


    @Override
    public void destroy() throws Exception {
        System.out.println(this.getClass().getName()+" destroyed by context (hopefully)");
    }
}
