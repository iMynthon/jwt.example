package mynthon.jwt.example.aop.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class AOPService {


    @Around("@within(mynthon.jwt.example.aop.service.Loggable)")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getMethod().getDeclaringClass().getSimpleName();
        String methodName = className + " - " + signature.getName();
        log.info("Before {}, args=[{}]", methodName, maskedPrivateData(className, joinPoint.getArgs()));
        StopWatch time = new StopWatch();
        time.start();
        Object result = joinPoint.proceed();
        time.stop();
        String stopWatchOutput = String.format("Method %s executed in %f seconds", methodName,
                time.getTotalTimeSeconds());
        log.info("Execution metrics: {}", stopWatchOutput);
        log.info("After: - {}", result);
        return result;
    }

    private Object maskedPrivateData(String className, Object[] args) {
        return className.equals("JwtTokenService") ? "********" : args;
    }
}
