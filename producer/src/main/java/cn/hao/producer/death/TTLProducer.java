package cn.hao.producer.death;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;

public class TTLProducer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();

        //声明普通交换机
        channel.exchangeDeclare(Constant.NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);


        AMQP.BasicProperties properties = new AMQP.BasicProperties()
                .builder()
                .expiration("10000")
                .build();
        for (int i = 1; i < 11; i++) {
            String msg = "消息内容：" + i;
            channel.basicPublish(Constant.NORMAL_EXCHANGE, Constant.CONSUMER_ROUTING_KEY, properties, msg.getBytes(StandardCharsets.UTF_8));
        }


    }

}
