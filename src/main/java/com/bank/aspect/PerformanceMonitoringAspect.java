package com.bank.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    // Matches all methods inside BankAccountService
    @Pointcut("execution(* com.bank.service.BankAccountService.*(..))")
    public void serviceLayerPointcut() {}

    @Around("serviceLayerPointcut()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();
        log.info("Starting execution: {}", methodName);

        try {
            Object result = joinPoint.proceed(); // Proceed with target method invocation
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("Completed execution: {} in {} ms", methodName, duration);
            if (duration > 500) {
                log.warn("PERFORMANCE WARNING: Method {} took {} ms to process!", methodName, duration);
            }
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed execution: {} after {} ms with error: {}", methodName, duration, ex.getMessage());
            throw ex;
        }
    }
}
