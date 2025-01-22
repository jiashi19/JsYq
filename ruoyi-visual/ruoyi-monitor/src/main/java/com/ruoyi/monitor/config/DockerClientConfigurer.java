package com.ruoyi.monitor.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerClientConfigurer {


    @Value("${docker.host}")
    private String dockerHost;

    @Value("${docker.network.name}")
    private String dockerNetworkName;
    @Bean
    public DockerClientConfig dockerClientConfig(){
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();
    }

    @Bean
    public DockerClient dockerClient(DockerClientConfig dockerClientConfig){
        return DockerClientBuilder.getInstance(dockerClientConfig).build();
    }

    @Bean
    public HostConfig hostConfig(){
        return new HostConfig().withNetworkMode(dockerNetworkName);
    }
}
