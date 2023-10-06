package cn.hao.producer.death;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class DeadConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("死信队列接受到的消息：" + msg);
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("死信队列被取消的消息：" + consumerTag);
        };
        channel.basicConsume(Constant.DEAD_CONSUMER_QUEUE, true, deliverCallback, cancelCallback);
    }
}
