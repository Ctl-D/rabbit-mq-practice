package cn.hao.producer.confirm;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class ConfirmMessage {

    public static final String CONFIRM_QUEUE = "CONFIRM_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQUtils.getChannel();

        //开启消息确认模式
        channel.confirmSelect();

        //队列持久化
        channel.queueDeclare(CONFIRM_QUEUE, true, false, false, null);


        //单个消息确认发布  耗时大
//        confirmMessage(channel);
        //批量消息确认发布  耗时小 但是不能确认丢失的消息
//        batchConfirmMessage(channel);
        //异步消息确认发布  耗时小 推荐使用
        asyncConfirmMessage(channel);
    }

    /**
     * 每发一条消息确认一次
     *
     * @param channel
     * @throws IOException
     * @throws InterruptedException
     */
    public static void confirmMessage(Channel channel) throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String msg = "单个消息内容" + i;
            //消息持久化
            channel.basicPublish("", CONFIRM_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes(StandardCharsets.UTF_8));
            channel.waitForConfirms();
        }
        long end = System.currentTimeMillis();

        System.out.println("单个消息发送确认，1000条消息总耗时" + (end - start) + "ms");
    }

    /**
     * 一次性全部发送之确认最后一次
     *
     * @param channel
     * @throws IOException
     * @throws InterruptedException
     */
    public static void batchConfirmMessage(Channel channel) throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String msg = "批量消息内容" + i;
            //消息持久化
            channel.basicPublish("", CONFIRM_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes(StandardCharsets.UTF_8));
            if (i % 1000 == 0) {
                channel.waitForConfirms();

            }
        }
        long end = System.currentTimeMillis();

        System.out.println("批量消息发送确认，1000条消息总耗时" + (end - start) + "ms");
    }


    public static void asyncConfirmMessage(Channel channel) throws IOException {

        //消息发送给rabbitmq的记录存储map
        ConcurrentSkipListMap<Long, String> outStandingMessage = new ConcurrentSkipListMap<>();

        //消息发送成功确认回调
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            //将发送成功消息从map移除
            if (multiple) {
                ConcurrentNavigableMap<Long, String> batchAckMsgMap = outStandingMessage.headMap(deliveryTag);
                batchAckMsgMap.forEach((key, value) -> {
                    System.out.println("确认的消息key：" + key + "::::::" + "确认的消息value：" + batchAckMsgMap.remove(key));
                });
            } else {
                System.out.println("确认的消息key：" + deliveryTag + "::::::" + "确认的消息value：" + outStandingMessage.remove(deliveryTag));
            }
        };

        //消息发送未确认回调
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            //将失败的消息打印输出
            if (multiple) {
                ConcurrentNavigableMap<Long, String> batchAckMsgMap = outStandingMessage.headMap(deliveryTag);
                batchAckMsgMap.forEach((key, value) -> {
                    System.out.println("未确认的消息key：" + key + "::::::" + "未确认的消息value：" + value);
                });
            } else {
                System.out.println("未确认的消息key：" + deliveryTag + "::::::" + "未确认的消息value：" + outStandingMessage.get(deliveryTag));
            }
        };

        channel.addConfirmListener(ackCallback, nackCallback);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String msg = "异步消息内容" + i;

            long nextPublishSeqNo = channel.getNextPublishSeqNo();
            //MessageProperties.PERSISTENT_TEXT_PLAIN开启消息持久化
            channel.basicPublish("", CONFIRM_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes(StandardCharsets.UTF_8));

            //将发送的消息记录起来
            outStandingMessage.put(nextPublishSeqNo, msg);
        }
        long end = System.currentTimeMillis();

        System.out.println("异步消息发送确认，1000条消息总耗时" + (end - start) + "ms");
    }

}
