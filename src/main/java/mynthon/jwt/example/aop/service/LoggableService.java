package mynthon.jwt.example.aop.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggableService {

    @Pointcut("execution(public * social.network.account.service.*.*(..))")
    public void loggableServiceClass(){}

    @Around("loggableServiceClass()")
    public Object beforeCallAtMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getMethod().getDeclaringClass().getSimpleName();
        String methodName = className + " - " + signature.getName();
        log.info("before {}, args=[{}]",methodName,maskedPrivateData(className, joinPoint.getArgs()));
        StopWatch time = new StopWatch();
        time.start();
        try {
            log.info("after {}",joinPoint.proceed());
            return joinPoint.proceed();
        } finally {
            time.stop();
            String stopWatchOutput = String.format("Method %s executed in %f seconds", methodName,
                    time.getTotalTimeSeconds());
            log.info("Execution metrics: {}", stopWatchOutput);
        }
    }

    private Object maskedPrivateData(String className,Object[] args){
        return className.equals("JwtTokenService") ? "********" : args;
    }
}
