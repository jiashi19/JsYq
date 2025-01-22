package com.ruoyi.monitor.controller;

import com.ruoyi.monitor.instant.R;
import com.ruoyi.monitor.service.DockerService;
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

    /**
     * 扩容节点
     * @param serviceName
     */
    @GetMapping("/expand")
    public R expandPod(@RequestParam(value = "serv") String serviceName){
        dockerService.createAndStartContainer(serviceName);
        return R.ok();
    }

    /**
     * 停止容器
     */
//    @GetMapping("/stop")
//    public R stopContainer(@RequestParam String containerId) {
//    }
}
