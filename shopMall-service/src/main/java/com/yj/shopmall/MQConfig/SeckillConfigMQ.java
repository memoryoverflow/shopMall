package com.yj.shopmall.MQConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yj.shopmall.constant.Constant.*;

@Configuration
public class SeckillConfigMQ {


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
    @Bean("SeconkillQueue")
    public Queue orderQueue(){
        return new Queue(SECKILL_QUEUE,true,false,false);
    }

    //创建一个交换器
    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(SECKILL_EXCHANGE);
    }

    //将交换器和队列绑定
    @Bean
    public Binding bindingExchangeWithQueue(@Qualifier("SeconkillQueue") Queue OrderQueue, DirectExchange exchange){
        return BindingBuilder.bind(OrderQueue).to(exchange).with(SECKILL_KEY);
    }
}
