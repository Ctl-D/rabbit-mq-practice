package cn.hao.producer.work;

import cn.hao.producer.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class AckConsumer {

    public static void main(String[] args) {
        AckWork ackWork1 = new AckWork(1);
        AckWork ackWork2 = new AckWork(10);

        new Thread(ackWork1, "Thread-0").start();
        new Thread(ackWork2, "Thread-1").start();

    }

}

class AckWork implements Runnable {

    public static final String QUEUE_NAME = "WORK_QUEUE";

    private int sleepTime;

    public AckWork(int sleepTime) {
        this.sleepTime = sleepTime;

    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        try {
            Channel channel = RabbitMQUtils.getChannel();
            System.out.println("线程" + threadName + "等待消息中……");
            channel.basicConsume(QUEUE_NAME, false, (var1, var2) -> {
                try {
                    TimeUnit.SECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String msg = new String(var2.getBody(), StandardCharsets.UTF_8);
                System.out.println("线程" + threadName + "接受到的消息内容：" + msg);

            }, (var1) -> System.out.println("线程" + threadName + "消息取消"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}