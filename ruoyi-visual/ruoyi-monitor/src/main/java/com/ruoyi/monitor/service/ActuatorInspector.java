package com.ruoyi.monitor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.monitor.entities.BaseException;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.services.InstanceRegistry;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * 访问actuator接口并获取结果
 *
 */
@Component
public class ActuatorInspector {

    @Autowired
    private InstanceRegistry instanceRegistry;

    private final Logger logger=LoggerFactory.getLogger(ActuatorInspector.class);
    private String pcu="process.cpu.usage";

    private int timeout=2;
    private WebClient webClient;
    private String actuatorMetricsUrlPrefix="/actuator/metrics/";

    public ActuatorInspector() {
//        webClient=WebClient.builder()
//                .build();
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeout)) // 响应超时
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout*1000); // 连接超时

        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public void test(){
        Flux<Instance> instancesFlux =instanceRegistry.getInstances();
        instancesFlux.subscribe(instance -> {
            checkProcessCpuUsage(instance);
        });
    }

    public double checkProcessCpuUsage(Instance instance) {
        String servUrl=instance.getRegistration().getServiceUrl();
        if (instance.getStatusInfo().getStatus().equals("UP")) {
            try {
                JsonNode response = webClient.get()
                        .uri(servUrl + actuatorMetricsUrlPrefix + pcu)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

                if (response != null) {
                    double val = extractFieldFromJson(response);
                    return Double.parseDouble(String.format("%.5f", val));
                } else {
                    logger.warn("Response is null");
                    return 0; // 或者抛出异常
                }
            } catch (Exception err) {
                logger.warn("Error fetching CPU usage: " + err.getMessage());
                return 0; // 或者抛出异常
            }
        } else {
            return 0;
        }
    }

    public double extractFieldFromJson(JsonNode jsonNode){
        JsonNode measurements = jsonNode.path("measurements");
        if (measurements.isArray() && measurements.size() > 0) {
            // 提取第一个 measurement 的 value 字段
            return measurements.get(0).path("value").asDouble();
            // 这里可以根据需要处理 cpuUsageValue
        } else {
            logger.error("Invalid JSON format from http response.");
            throw new BaseException("Invalid JSON format from http response.");
        }
    }
}
