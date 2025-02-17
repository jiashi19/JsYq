package com.ruoyi.monitor.task;

import com.ruoyi.monitor.entities.events.CpuThresholdEvent;
import com.ruoyi.monitor.service.ActuatorInspector;
import com.ruoyi.monitor.service.DockerService;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.services.InstanceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PodMonitorTask {
    private static final Logger logger = LoggerFactory.getLogger(PodMonitorTask.class);

    @Autowired
    private ActuatorInspector actuatorInspector;

    @Autowired
    private InstanceRegistry instanceRegistry;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DockerService dockerService;
    private static final double CPU_THRESHOLD=0.8;

    private ConcurrentHashMap<String,Integer> exceededCounter;
    private static final int EXCEEDED_THRESHOLD=3;

    private static final long COOLDOWN_PERIOD = 150_000; // 冷却期（毫秒）
    private ConcurrentHashMap<String, Long> lastScaleTimeMap;
    public PodMonitorTask(){
        this.lastScaleTimeMap= new ConcurrentHashMap<>();
        this.exceededCounter=new ConcurrentHashMap<>();

    }

    /**
     * 定期单个检查
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void checkProcessCpuUsage() {
        Flux<Instance> instancesFlux =instanceRegistry.getInstances();
        instancesFlux
            .parallel()
            .runOn(Schedulers.parallel())
            .subscribe(instance -> {
                double val=actuatorInspector.checkProcessCpuUsage(instance);
                if(val>CPU_THRESHOLD){
                    //TODO sse WARNING 告警
                    eventPublisher.publishEvent(new CpuThresholdEvent(this,"cpu利用率高",instance));
                }
            });
    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void test(){
        eventPublisher.publishEvent(new CpuThresholdEvent(this,"test"));
    }



    /**
     * 检查每个服务下的所有实例的cpu使用情况
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void checkServiceCpuUsage() {
        Flux<Instance> instancesFlux =instanceRegistry.getInstances();
        instancesFlux
            .groupBy((instance) -> instance.getRegistration().getName())//按服务名分组
            .subscribe(group -> {
                String serviceName = group.key();
                group.parallel()
                    .runOn(Schedulers.parallel())
                    .map(instance -> actuatorInspector.checkProcessCpuUsage(instance))
                    .sequential()
                    .collectList()
                    .subscribe(vals -> {
                        List<Double> CpuUsageList=vals.stream().filter(val -> val>0).collect(Collectors.toList());
                        if (CpuUsageList.isEmpty()) {
                            return;
                        }

                        // 计算平均值
                        double averageCpuUsage = CpuUsageList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                        double maxCpuUsage = CpuUsageList.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                        double minCpuUsage = CpuUsageList.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                        double range = maxCpuUsage - minCpuUsage;

                        if (isInCooldown(serviceName)) {
                            logger.debug("服务 {} 处于冷却期，跳过扩容检查", serviceName);
                            return;
                        }
                        if (averageCpuUsage > CPU_THRESHOLD) {
                            int count = exceededCounter.merge(serviceName, 1, Integer::sum);
                            if (count >= EXCEEDED_THRESHOLD) {
                                performScaling(serviceName);
                            }
                        }
                        if(averageCpuUsage<CPU_THRESHOLD){
                            //TODO sse 告警: cpu利用率低
                            eventPublisher.publishEvent(new CpuThresholdEvent(this,serviceName+"服务 整体cpu利用率低"));
                        }
                        if(range>0.5){
                            //TODO sse 告警：负载不均衡
                        }

                    });
            });

    }
    /**
     * 扩容冷却期状态检查
     */
    private boolean isInCooldown(String serviceName) {
        Long lastScale = lastScaleTimeMap.get(serviceName);
        return lastScale != null &&
                (System.currentTimeMillis() - lastScale) < COOLDOWN_PERIOD;
    }

    /**
     * 执行扩容操作
     */
    private void performScaling(String serviceName) {
        try {
            dockerService.createAndStartContainer(serviceName);
            lastScaleTimeMap.put(serviceName, System.currentTimeMillis());
            exceededCounter.put(serviceName, 0);
            logger.info("服务 {} 扩容成功", serviceName);
            //TODO sse
        } catch (Exception e) {
            logger.error("服务 {} 扩容失败：{}", serviceName, e.getMessage());
        }
    }

}
