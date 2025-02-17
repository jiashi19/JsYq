package com.ruoyi.monitor.controller;

import com.ruoyi.monitor.entities.Re;
import com.ruoyi.monitor.service.DockerService;
import com.ruoyi.monitor.service.ActuatorInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pods/")
public class PodController {

    @Autowired
    private DockerService dockerService;

    @Autowired
    private ActuatorInspector actuatorInspector;

    /**
     * 扩容节点
     * @param serviceName
     */
    @GetMapping("/expand")
    public Re expandPod(@RequestParam(value = "serv") String serviceName){
        dockerService.createAndStartContainer(serviceName);
        return Re.ok();
    }

    /**
     * 根据Id停止容器
     */
    @GetMapping("/stop")
    public Re stopContainer(@RequestParam(value = "id") String containerId) {
        dockerService.stopContainer(containerId);
        return Re.ok();
    }

    /**
     * 根据ID删除容器
     * @param containerId
     * @return
     */
    @GetMapping("/remove")
    public Re removeContainer(@RequestParam(value = "id") String containerId) {
        dockerService.removeContainer(containerId);
        return Re.ok();
    }
    @GetMapping("/test")
    public Re test(){
        actuatorInspector.test();
        return Re.ok();
    }

}
