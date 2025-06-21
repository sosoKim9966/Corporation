package com.work.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync // 비동기 활성화 어노테이션
@Configuration
public class AsyncConfig {

    @Bean
    public Executor corporationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(32);

        // 공정모드, 우선순위, 지연 요소 고려하여 판단 -> LinkedBlockingQueue
        executor.setQueueCapacity(100);

        // 기본값 60
        executor.setKeepAliveSeconds(60);

        executor.setThreadNamePrefix("corporation-async");

        executor.setAwaitTerminationSeconds(60);
        // 서버 종료시 스레드 풀의 작업 중인 스레드와 대기 중인 큐의 작업이 모두 완료될때까지 스레드 풀의 종료를 지연시는 것
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 거절 정책 -> 작업이 스레드 풀의 최대 크기에 도달하고 큐가 가득 찼을때 적용
        /*
         AbortPolicy: 기본 거절 정책으로, 새로운 작업을 제출할 때, RejectedExecutionException 을 발생하며 거절합니다.
         DiscardPolicy: 새로운 작업을 조용히 버립니다.
         CallerRunPolicy: 새로운 작업을 제출한 스레드가 대신해서 직접 작업을 실행합니다. -> 트래픽이 많은 상황에서의 최소한의 작업 처리
         다만, 이러한 과정은 마치 큐가 꽉 차더라도 호출 스레드가 작업을 실행하면서, 시스템이 정상적으로 동작하는 것처럼 보일 수 있습니다.
         이로 인해 개발자가 과도한 트래픽 문제를 즉각 인지하기 어려울 수 있습니다.
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }


}
