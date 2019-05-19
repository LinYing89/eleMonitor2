package com.lygzbkj.elemonitor;

import java.io.IOException;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lygzbkj.elemonitor.comm.ManagerPublishReceiver;
import com.rabbitmq.client.Channel;

@Configuration
public class RabbitConfig {

    /**
     * 通信管理机配置报文接收交换机
     */
    public static final String TOPIC_MANAGER_CONFIG_EXCHAGE_NAME = "manager-config-exchange";
    /**
     * 通信管理机报文分发交换机
     */
    public static final String TOPIC_MANAGER_PUBLISH_EXCHAGE_NAME = "manager-publish-exchange";
    // 随机生成queue名称, 程序退出后自动删除
    public String queueName;

    @Bean
    Queue managerPublishQueue(ConnectionFactory rabbitConnectionFactory) {
        Connection connection = rabbitConnectionFactory.createConnection();
        Channel channel = connection.createChannel(false);
        try {
            // 随机生成queue名称, 程序退出后自动删除
            queueName = channel.queueDeclare().getQueue();
            System.out.println("queue name " + queueName);
            return new Queue(queueName);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通信管理机报文分发交换机
     * 
     * @return
     */
    @Bean
    TopicExchange managerPublishExchange() {
        return new TopicExchange(TOPIC_MANAGER_PUBLISH_EXCHAGE_NAME);
    }

    @Bean
    Binding bindingManagerPublish(Queue managerPublishQueue, TopicExchange managerPublishExchange) {
        return BindingBuilder.bind(managerPublishQueue).to(managerPublishExchange).with("com.zhibo.manager0");
    }

    // 设置rabbit监听器
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        // 不确认, (相当于自动确认?), 接收到即从队列中删除, 防止消费端抛出异常后, 重复接收消息
        container.setAcknowledgeMode(AcknowledgeMode.NONE);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(ManagerPublishReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMsg");
    }
}
