package cn.hao.producer.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TTLConfig {

    //普通交换机
    public static final String X_EXCHANGE = "X";
    //死信交换机
    public static final String Y_EXCHANGE = "Y";
    //普通队列A
    public static final String A_QUEUE = "A";
    //普通队列B
    public static final String B_QUEUE = "B";
    //死信队列QD
    public static final String QD_QUEUE = "QD";

    //普通交换机与A队列
    public static final String X_A_ROUTING_KEY = "XA";
    //普通交换机与B队列
    public static final String X_B_ROUTING_KEY = "XB";
    //死信交换机与QD队列
    public static final String Y_QD_ROUTING_KEY = "QD";

    //交换机声明-------------------------------------------------------------------------------------------------------
    @Bean("xExchange")
    public DirectExchange getExchangeX() {
        return new DirectExchange(X_EXCHANGE);
    }

    @Bean("yExchange")
    public DirectExchange getExchangeY() {
        return new DirectExchange(Y_EXCHANGE);
    }

    //队列声明---------------------------------------------------------------------------------------------------------
    @Bean("aQueue")
    public Queue getQueueA() {
        Map<String, Object> arguments = new HashMap<>();
        //设置死信交换机
        arguments.put("x-dead-letter-exchange", Y_EXCHANGE);
        //设置死信交换机路由
        arguments.put("x-dead-letter-routing-key", Y_QD_ROUTING_KEY);
        //设置延迟时间
        arguments.put("x-message-ttl", "10000");
        return QueueBuilder.durable(A_QUEUE).withArguments(arguments).build();
    }

    @Bean("bQueue")
    public Queue getQueueB() {
        Map<String, Object> arguments = new HashMap<>();
        //设置死信交换机
        arguments.put("x-dead-letter-exchange", Y_EXCHANGE);
        //设置死信交换机路由
        arguments.put("x-dead-letter-routing-key", Y_QD_ROUTING_KEY);
        //设置延迟时间
        arguments.put("x-message-ttl", "40000");
        return QueueBuilder.durable(B_QUEUE).withArguments(arguments).build();
    }

    @Bean("qdQueue")
    public Queue getQueueQD() {
        return QueueBuilder.durable(QD_QUEUE).build();
    }

    //绑定------------------------------------------------------------------------------------------------------------
    @Bean
    public Binding queueABindingX(@Qualifier("aQueue") Queue aQueue,
                                  @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(aQueue).to(xExchange).with(X_A_ROUTING_KEY);
    }

    @Bean
    public Binding queueBBindingX(@Qualifier("bQueue") Queue bQueue,
                                  @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(bQueue).to(xExchange).with(X_B_ROUTING_KEY);
    }

    @Bean
    public Binding queueQDBindingY(@Qualifier("qdQueue") Queue qdQueue,
                                   @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(qdQueue).to(yExchange).with(Y_QD_ROUTING_KEY);
    }
}
