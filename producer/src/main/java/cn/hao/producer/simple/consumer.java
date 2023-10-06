package cn.hao.producer.simple;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class consumer {

    public static String QUEUE_NAME = "ONE_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.176.101");
        factory.setUsername("admin");
        factory.setPassword("admin");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (var1, var2) -> {
            String msg = new String(var2.getBody(), StandardCharsets.UTF_8);
            System.out.println("接受到的消息内容：" + msg);
        };

        CancelCallback cancelCallback = (var1) -> {
            System.out.println("消费者取消消费");
        };

        /**
         * 参数声明
         * 1.队列名称
         * 2.消费是否自动确认
         * 3.消费者消费成功回调
         * 4.消费者取消消费回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
