package com.ruoyi.monitor.service;


public interface DockerService {


    void createAndStartContainer(String imageName);

    void stopContainer(String containerId);
    void removeContainer(String containerId);
}
