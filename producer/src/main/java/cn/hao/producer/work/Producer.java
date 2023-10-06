package cn.hao.producer.work;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static final String QUEUE_NAME = "WORK_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //队列持久化
//        channel.queueDeclare(QUEUE_NAME, true, false, false, null);


        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String msg = scanner.next();
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
            //消息持久化
//            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes(StandardCharsets.UTF_8));
            System.out.println(msg + "消息已发送");
        }

    }
}
