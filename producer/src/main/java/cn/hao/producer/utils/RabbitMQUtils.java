package cn.hao.producer.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQUtils {

    public static Channel getChannel() throws IOException, TimeoutException {
        //创建一个链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置链接rabbitmq的ip
        factory.setHost("192.168.176.101");
        //用户名
        factory.setUsername("admin");
        //密码
        factory.setPassword("admin");

        //创建链接
        Connection connection = factory.newConnection();

        return connection.createChannel();
    }
}
