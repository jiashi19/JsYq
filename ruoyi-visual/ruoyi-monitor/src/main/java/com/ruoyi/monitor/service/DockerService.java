package com.ruoyi.monitor.service;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;

public interface DockerService {


    public void createAndStartContainer(String imageName);
}
