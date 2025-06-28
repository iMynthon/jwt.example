package mynthon.jwt.example.aop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class AOPController {

    @Around("@within(mynthon.jwt.example.aop.service.LoggableController)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable{
        ContentCachingRequestWrapper requestWrapper = cacheRequest();
        log.info("Request Param: - {}",param(requestWrapper));
        log.info("Request Body: - {}",requestBody(requestWrapper));
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + " - " + signature.getName();
        StopWatch time = new StopWatch();
        time.start();
        Object result = joinPoint.proceed();
        time.stop();
        String stopWatchOutput = String.format("method %s executed in %f seconds",methodName,time.getTotalTimeSeconds());
        log.info("Execution metrics controller - {}",stopWatchOutput);
        log.info("After: - {}",result);
        return result;
    }

    private ContentCachingRequestWrapper cacheRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return new ContentCachingRequestWrapper(request);
    }

    private String requestBody(ContentCachingRequestWrapper requestWrapper) throws Throwable{
        String requestBody = new String(requestWrapper.getContentAsByteArray(),requestWrapper.getCharacterEncoding());
        return requestBody.isEmpty() ? "Empty is requestBody" : requestBody;
    }

    private String param(ContentCachingRequestWrapper requestWrapper){
        return requestWrapper.getParameterMap().isEmpty() ? "empty param" : requestWrapper.getParameterMap().entrySet().stream()
                .map(p -> String.format("%s=%s", p.getKey(), String.join(",", p.getValue())))
                .collect(Collectors.joining("&"));

    }
}
