package com.bank.aspect;

import com.bank.annotation.AuditLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class BankingAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(BankingAuditAspect.class);

    // Matches methods annotated with @AuditLog
    @AfterReturning(pointcut = "@annotation(auditLog)", returning = "result")
    public void logSuccessfulTransaction(JoinPoint joinPoint, AuditLog auditLog, Object result) {
        String action = auditLog.action();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[AUDIT SUCCESS] Action: [{}] | Method: {} | Arguments: {} | Return Payload: {}",
                action, methodName, Arrays.toString(args), result);
    }

    @AfterThrowing(pointcut = "@annotation(auditLog)", throwing = "exception")
    public void logFailedTransaction(JoinPoint joinPoint, AuditLog auditLog, Exception exception) {
        String action = auditLog.action();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.error("[AUDIT FAILURE] Action: [{}] | Method: {} | Arguments: {} | Error: {}",
                action, methodName, Arrays.toString(args), exception.getMessage());
    }
}
