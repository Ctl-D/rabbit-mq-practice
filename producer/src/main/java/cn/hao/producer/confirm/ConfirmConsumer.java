package cn.hao.producer.confirm;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ConfirmConsumer {
    public static final String CONFIRM_QUEUE = "CONFIRM_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("接受到的消息：" + msg);
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("被取消的消息：" + consumerTag);
        };
        channel.basicConsume(CONFIRM_QUEUE, false, deliverCallback, cancelCallback);
    }
}
