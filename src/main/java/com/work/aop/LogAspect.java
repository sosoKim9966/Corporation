package com.work.aop;

import com.work.exception.CustomException;
import com.work.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
@Component
public class LogAspect {

    @Around("serviceLayer()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        String sig = joinPoint.getSignature().toShortString();

        try {
            log.info("[TRANSACTION START] {}", sig);
            Object result = joinPoint.proceed();
            log.info("[TRANSACTION COMMIT] sig={}, (elapsed={}ms)", sig, elapsedMs(start));

            return result;
        } catch (CustomException customException) {
            log.warn("[TRANSACTION PROPAGATE] sig={}, code={}", sig, customException.getErrorCode());

            throw customException;
        } catch (Exception e) {
            log.error("[TRANSACTION ROLLBACK] sig={}, message={}", sig, e.getMessage());

            throw new CustomException(ErrorCode.TRANSACTION_ROLLBACK, e.getMessage(), e);
        } finally {
            log.info("[TRANSACTION FINALLY] sig={}", sig);
        }
    }

    private static long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    /** 서비스 계층만 (…service 패키지 하위의 @Service 클래스) */
    @Pointcut("execution(public * com.work..*Service.*(..))")
    public void serviceLayer() {}

}
