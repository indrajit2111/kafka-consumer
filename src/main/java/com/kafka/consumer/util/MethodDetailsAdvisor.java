package com.kafka.consumer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
public class MethodDetailsAdvisor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodDetailsAdvisor.class);

    @Autowired
    private Util util;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * This method is used to print the Method name and Class Name
     *
     * @param joinPoint
     * @return Object
     * @throws Throwable
     */
    @Around("@annotation(com.kafka.consumer.util.MethodDetails) && execution(* * (..))")
    public Object methodDetails(final ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = Optional.empty();
        var message = Optional.empty().toString();
        final var stopWatch = new StopWatch();
        try {
            message = getMessage(joinPoint);
            LOGGER.info("ENTRY :: {} Current Start Time {}", message, System.currentTimeMillis());
            stopWatch.start();
            logMethodArgument(joinPoint.getArgs(), message);
            result = joinPoint.proceed();
        } catch (final Throwable exception) {
            if (!(exception instanceof ClassCastException)) {
                exceptionToString(exception);
            }
            throw exception;
        } finally {
            logResult(result, message);
            stopWatch.stop();
            LOGGER.info("EXIT :: {} Current End Time :: {} with Elapsed Time (in Milliseconds) :: {}", message,
                    System.currentTimeMillis(), stopWatch.getTotalTimeMillis());
        }
        return result;
    }

    private void logMethodArgument(final Object[] objects, final String message) {

        if (Objects.nonNull(objects)) {
            final Object[] varA = objects;
            for (var varB = 0; varB < objects.length; ++varB) {
                printLog(varA[varB], message);
            }
        }
    }

    private void logResult(final Object object, final String message) {

        printLog(object, message);
    }

    private String getMessage(final JoinPoint joinPoint) {

        final var sb = new StringBuilder();
        final var methodSignature = (MethodSignature) joinPoint.getSignature();
        final var method = methodSignature.getMethod();
        sb.append(method.getDeclaringClass()).append(".").append(method.getName()).append(" ");
        return sb.toString();
    }

    private void printLog(final Object object, final String message) {

        try {
            final var logs = objectMapper.writeValueAsString(object);
            final var logInfo = String.format("%s :: %s", message, logs);
            LOGGER.info(logInfo);
        } catch (final Exception exception) {
            exceptionToString(exception);
        }
    }

    private void exceptionToString(final Throwable throwable) {
        LOGGER.error("Exception in LoggerAroundAdvisor.methodDetails() :: {}", util.getCauseMessage(throwable));
    }
}

