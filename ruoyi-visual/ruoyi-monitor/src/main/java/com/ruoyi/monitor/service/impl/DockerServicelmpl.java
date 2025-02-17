package com.ruoyi.monitor.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.ruoyi.monitor.entities.BaseException;
import com.ruoyi.monitor.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.UUID;

@Service
public class DockerServicelmpl implements DockerService {

    private final Logger logger= LoggerFactory.getLogger(DockerServicelmpl.class);
    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private HostConfig hostConfig;



    // 创建并启动容器
    public void createAndStartContainer(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            logger.error("Docker client or image name is invalid.");
            throw new IllegalArgumentException("Docker client or image name is invalid.");
        }

        String containerName=imageName+"-"+generateRandomSuffix();
        try{
            //创建容器
            CreateContainerResponse ccr = dockerClient.createContainerCmd(imageName)
                    .withName(containerName)
                    .withHostConfig(hostConfig)
                    .withCmd("--spring.cloud.nacos.discovery.server-addr=ruoyi-nacos:8848",
                            "--spring.cloud.nacos.config.server-addr=ruoyi-nacos:8848")
                    .exec();

            // 启动容器
            dockerClient.startContainerCmd(ccr.getId()).exec();
            logger.info("start container {} successfully", ccr.getId());
        }catch (Exception e){
            logger.error("Failed to create and start container", e);
            throw new BaseException("Failed to create and start container");
        }

    }
    // 停止容器
    public void stopContainer(String containerId) {
        if (containerId == null || containerId.trim().isEmpty()) {
            logger.error("Invalid container ID: {}", containerId);
            throw new IllegalArgumentException("Container ID cannot be null or empty");
        }
        try {
            logger.info("Stopping container with ID: {}", containerId);
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (Exception e) {
            logger.error("Failed to stop container with ID: {}", containerId);
            throw new BaseException("Failed to stop container");
        }
    }

    // 删除容器
    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }


    //产生随机容器名称后缀
    private String generateRandomSuffix() {
        // 生成随机字符串
        return UUID.randomUUID().toString().substring(0, 4);
    }
}
