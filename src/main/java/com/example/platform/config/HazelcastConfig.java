package com.example.platform.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HazelcastConfig {

    @Bean
    public Config hazelcastConfig() {
        Config config=new Config();
        config.setInstanceName("hazelcast-instance");

        config.addMapConfig(
                new MapConfig()
                        .setName("getinvestors")
                        .setTimeToLiveSeconds(300)
                );

        return config;


    }

}

