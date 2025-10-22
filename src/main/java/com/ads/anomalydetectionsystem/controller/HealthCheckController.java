package com.ads.anomalydetectionsystem.controller;

import com.ads.anomalydetectionsystem.service.CloudWatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    private final CloudWatchService cloudWatchService;

    public HealthCheckController(CloudWatchService cloudWatchService) {
        this.cloudWatchService = cloudWatchService;
    }

    @GetMapping("/health")
    public String checkHealth() {
        return "ok";
    }

    @GetMapping("/metrics/ec2-cpu")
    public String getEc2CpuMetrics() {
        cloudWatchService.getEc2CpuUtilization();

        return "EC2 CPU 사용률 조회를 요청했습니다. 서버 콘솔 로그를 확인하세요.";
    }
}
