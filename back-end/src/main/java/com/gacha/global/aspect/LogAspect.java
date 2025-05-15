package com.gacha.global.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j // private static final Logger log = LoggerFactory.getLogger(클래스); 대체 Annotation
@Aspect // 관점 지향 설정
@Component
public class LogAspect {

    @Around("execution(* com.gacha.controller..*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint, "Controller");
    }

    @Around("execution(* com.gacha.model.service..*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint, "Service");
    }

    @Around("execution(* com.gacha.model.dao..*.*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint, "Repository");
    }

    private Object logExecutionTime(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 요청 정보 로깅 (Controller에만 해당)
        if ("Controller".equals(type)) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            log.info("[REQ] {} | {} | {}", request.getMethod(), request.getRequestURI(), joinPoint.getArgs());
        }

        log.info("[{}] {}.{} | START | args: {}",
                type, className, methodName, joinPoint.getArgs());

        // 메소드 실행
        Object result = joinPoint.proceed();

        stopWatch.stop();

        // 응답 정보 로깅
        log.info("[{}] {}.{} | END | time: {}ms | return: {}",
                type, className, methodName, stopWatch.getTotalTimeMillis(), result);

        return result;
    }
}
