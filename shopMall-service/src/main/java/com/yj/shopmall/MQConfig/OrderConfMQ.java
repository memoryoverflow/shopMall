package com.yj.shopmall.MQConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yj.shopmall.constant.Constant.*;

@Configuration
public class OrderConfMQ {
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("106.14.226.138", 5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    //穿件一个 队列 名为
    @Bean("OrderQueue")
    public Queue orderQueue(){
        return new Queue(ORDER_QUEUE,true,false,false);
    }

    //创建一个交换器
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(ORDER_EXCHANGE);
    }

    //将交换器和队列绑定
    @Bean
    public Binding bindingExchangeWithQueue(@Qualifier("OrderQueue") Queue OrderQueue, TopicExchange exchange){
        return BindingBuilder.bind(OrderQueue).to(exchange).with(ORDERQUEUE_KEY);
    }
}
