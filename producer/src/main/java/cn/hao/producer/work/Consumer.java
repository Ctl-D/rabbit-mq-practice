package cn.hao.producer.work;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            Work work = new Work();
            new Thread(work, "Thread-" + i).start();
        }
    }
}


class Work implements Runnable {

    public static final String QUEUE_NAME = "WORK_QUEUE";

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        try {
            Channel channel = RabbitMQUtils.getChannel();
            System.out.println("线程" + threadName + "等待消息中……");
            channel.basicConsume(QUEUE_NAME, true, (var1, var2) -> {
                String msg = new String(var2.getBody(), StandardCharsets.UTF_8);
                System.out.println("线程" + threadName + "接受到的消息内容：" + msg);
            }, (var1) -> {
                System.out.println("线程" + threadName + "消息取消");
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }
}