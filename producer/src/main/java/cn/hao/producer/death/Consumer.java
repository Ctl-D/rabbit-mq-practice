package cn.hao.producer.death;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Consumer {


    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();


        //声明普通交换机和死信交换机
        channel.exchangeDeclare(Constant.NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(Constant.DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //配置消费队列和死信交换机的信息
        Map<String, Object> arguments = new HashMap<>();
        //消费者设置死信交换机
        arguments.put("x-dead-letter-exchange",Constant.DEAD_EXCHANGE);
        //设置死信交换机的RoutingKey
        arguments.put("x-dead-letter-routing-key",Constant.DEAD_CONSUMER_ROUTING_KEY);
        //普通队列消息最大长度
//        arguments.put("x-max-length","6");

        //声明消费队列和死信消费队列
        channel.queueDeclare(Constant.CONSUMER_QUEUE, true, false, false, arguments);
        channel.queueDeclare(Constant.DEAD_CONSUMER_QUEUE, true, false, false, null);

        //消费队列和普通交换机绑定
        channel.queueBind(Constant.CONSUMER_QUEUE, Constant.NORMAL_EXCHANGE, Constant.CONSUMER_ROUTING_KEY);

        //死信队列和死信交换机绑定
        channel.queueBind(Constant.DEAD_CONSUMER_QUEUE, Constant.DEAD_EXCHANGE, Constant.DEAD_CONSUMER_ROUTING_KEY);

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("接受到的消息：" + msg);
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("被取消的消息：" + consumerTag);
        };
        channel.basicConsume(Constant.CONSUMER_QUEUE, true, deliverCallback, cancelCallback);

    }
}
