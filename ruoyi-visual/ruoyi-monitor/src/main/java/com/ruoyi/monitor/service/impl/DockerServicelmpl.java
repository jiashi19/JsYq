package com.ruoyi.monitor.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.ruoyi.monitor.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DockerServicelmpl implements DockerService {

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private HostConfig hostConfig;

    //产生随机容器名称后缀
    private String generateRandomSuffix() {
        // 生成随机字符串
        return UUID.randomUUID().toString().substring(0, 4);
    }

    // 创建并启动容器
    public void createAndStartContainer(String imageName) {
        String containerName=imageName+"-"+generateRandomSuffix();
        //创建容器
        CreateContainerResponse ccr = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withCmd("--spring.cloud.nacos.discovery.server-addr=ruoyi-nacos:8848",
                        "--spring.cloud.nacos.config.server-addr=ruoyi-nacos:8848")
                .exec();

        // 启动容器
        dockerClient.startContainerCmd(ccr.getId()).exec();
    }


}
