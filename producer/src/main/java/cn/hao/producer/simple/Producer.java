package cn.hao.producer.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static String QUEUE_NAME = "ONE_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {

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

        Channel channel = connection.createChannel();

        /**
         * 参数说明
         * 1.队列名称
         * 2.是否消息持久化到磁盘 true放在磁盘 false放在内存
         * 3.是否消息独占
         * 4.是否自动删除
         * 5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String msg = "测试消息发送";

        /**
         * 参数说明
         * 1.发送到的交换机
         * 2.路由的key值
         * 3.其他参数信息
         * 4.发送的消息体
         */
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
        System.out.println("msg send success");
    }
}
