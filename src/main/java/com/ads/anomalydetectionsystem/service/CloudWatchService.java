package com.ads.anomalydetectionsystem.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
public class CloudWatchService {

    // AWS CloudWatch 통신 객체
    private final CloudWatchClient cloudWatchClient;

    /**
     * 생성자 주입 (Constructor Injection)
     * Spring이 이 서비스를 빈으로 생성할 때, IoC 컨테이너에서
     * 'spring-cloud-aws-starter'가 application.yml의 설정으로 미리 만들어 둔
     * CloudWatchClient 빈을 자동으로 찾아 여기에 주입(DI)해 줍니다.
     *
     * @param cloudWatchClient Spring IoC 컨테이너에 의해 주입되는 AWS CloudWatch 클라이언트
     */
    public CloudWatchService(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    /**
     * EC2 인스턴스의 CPU 사용률을 가져오는 메서드
     */
    public void getEc2CpuUtilization() {
        try {


            // 1. 조회 기간 설정 : 지금부터 10분 전까지의 데이터 범위로 지정
            Instant endTime = Instant.now();
            Instant startTime = endTime.minus(10, ChronoUnit.MINUTES);

            // 2. 어떤 EC2 인스턴스의 메트릭을 가져올지 지정합니다.
            // Dimension은 메트릭을 필터링하는 조건입니다. '어떤 EC2 인스턴스?' 라는 질문에 답하는 부분이죠.
            // (EC2 콘솔 -> 인스턴스 -> monitoring-target-server 클릭 -> 인스턴스 ID 확인)
            Dimension dimension = Dimension.builder()
                    .name("InstanceId")
                    .value("i-0a8837e753578b380") // instance-id
                    .build();

            // 3. 어떤 메트릭을 가져올지 상세하게 정의합니다.
            // Namespace: 서비스 종류 (EC2, RDS 등)
            // MetricName: 메트릭 이름 (CPU 사용률, 네트워크 입출력 등)
            Metric metric = Metric.builder()
                    .namespace("AWS/EC2")
                    .metricName("CPUUtilization")
                    .dimensions(dimension)
                    .build();

            // 4. 메트릭을 어떻게 계산할지 정의합니다.
            // Period: 데이터 집계 간격 (초 단위). 60초(1분) 간격으로 집계합니다.
            // Stat: 통계 방식 (평균, 합계, 최대, 최소). 1분 동안의 CPU 사용률 '평균(Average)'을 구합니다.
            MetricStat metricStat = MetricStat.builder()
                    .metric(metric)
                    .period(60)
                    .stat("Average")
                    .build();

            // 5. 위에서 정의한 내용을 바탕으로 최종 쿼리를 만듭니다.
            // Id는 그냥 이 쿼리의 별명입니다
            MetricDataQuery dataQuery = MetricDataQuery.builder()
                    .id("m1")
                    .metricStat(metricStat)
                    .returnData(true)
                    .build();

            // 6. 최종 요청서를 만듭니다
            GetMetricDataRequest request = GetMetricDataRequest.builder()
                    .startTime(startTime)
                    .endTime(endTime)
                    .metricDataQueries(dataQuery)
                    .build();

            // 7. AWS CloudWatch에 요청을 보내고 응답을 받습니다.
            GetMetricDataResponse response = cloudWatchClient.getMetricData(request);

            // 8. 응답 결과를 콘솔에 출력합니다.
            System.out.println("--- EC2 CPU 사용률 (지난 10분)---");
            for (int i = 0; i < response.metricDataResults().get(0).timestamps().size(); i++) {
                System.out.println("시간: " + response.metricDataResults().get(0).timestamps().get(i) +
                        ", CPU 사용률(%): " + response.metricDataResults().get(0).values().get(i));
            }
        } catch (Exception e) {
            System.err.println("에러 발생 : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
