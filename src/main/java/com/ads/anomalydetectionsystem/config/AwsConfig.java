package com.ads.anomalydetectionsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

/**
 * @Configuration:
 * "Spring아, 이 클래스는 단순한 클래스가 아니야.
 * 프로젝트의 설정을 담당하는 특별한 '설정 파일'로 사용해 줘."
 * Spring은 이 꼬리표가 붙은 클래스를 먼저 읽어서 환경설정을 구성합니다.
 */
@Configuration
public class AwsConfig {

    /**
     * @Bean:
     * "이 메서드(cloudWatchClient)가 반환(return)하는 객체를
     * Spring IoC 컨테이너(빈 바구니)에 'Bean(빈)'으로 등록해 줘."
     *
     * 이 메서드가 바로 우리가 찾던 'CloudWatchClient 완제품'을 만드는 공장입니다.
     */
    @Bean
    public CloudWatchClient cloudWatchClient(
            // Spring이 'spring-cloud-aws-starter'를 이용해 application.yml에서
            // 키(Key) 정보를 읽어 '자동으로' 만들어 둔 '신분증(Provider)' 빈을 여기에 주입(DI)시켜 줍니다.
            AwsCredentialsProvider credentialsProvider
    ) {
        // application.yml에 설정된 'ap-northeast-2' 값을 사용하여 서울 리전을 지정합니다.
        Region region = Region.AP_NORTHEAST_2;

        // 주입받은 '신분증(credentialsProvider)'과 우리가 지정한 '지역(region)' 정보를 사용해서
        // 'CloudWatchClient 완제품'을 조립(build)합니다.
        return CloudWatchClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }
}
