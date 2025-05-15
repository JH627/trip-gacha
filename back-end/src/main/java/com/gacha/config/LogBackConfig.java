package com.gacha.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogBackConfig {

    @PostConstruct
    public void logbackConfigurer() {

        // 1. LoggerContext 객체를 가져온다.
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 2. ConsoleAppender 생성 및 설정
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setName("STDOUT");

        // 3. PatternLayoutEncoder 생성 및 설정 (콘솔용)
        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(loggerContext);
        consoleEncoder.setPattern("%d{HH:mm:ss} [%thread] [%-5level] %C{1}.%M.%L- %msg%n");
        consoleEncoder.start();

        // 4. Appender에 encoder 설정
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();

        // 5. RollingFileAppender 생성 및 설정
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setName("FILE");
        rollingFileAppender.setFile("logFile.log");

        // 6. TimeBasedRollingPolicy 생성 및 설정
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setFileNamePattern("logFile.%d{yyyy-MM-dd}.log");
        rollingPolicy.setMaxHistory(30);
        rollingPolicy.setTotalSizeCap(new FileSize(3 * FileSize.GB_COEFFICIENT));
        rollingPolicy.start();

        // 7. 롤링 파일 어펜더에 롤링 정책 설정
        rollingFileAppender.setRollingPolicy(rollingPolicy);

        // 8. PatternLayoutEncoder 생성 및 설정 (파일용)
        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setContext(loggerContext);
        fileEncoder.setPattern("%d{HH:mm:ss.SSS} %-4relative [%thread] %-5level %logger{35} -%kvp- %msg%n");
        fileEncoder.start();

        // 9. 롤링 파일 어펜더에 인코더 설정
        rollingFileAppender.setEncoder(fileEncoder);
        rollingFileAppender.start();

        // 10. Root 로거 설정 (ch.qos.logback.classic 패키지)
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO); // 어느 레벨 로그부터 출력할 지 설정.

        // 11. 특정 패키지 로거 설정
        Logger appLogger = loggerContext.getLogger("com.gacha");
        appLogger.setLevel(Level.DEBUG);

//        rootLogger.addAppender(consoleAppender);
        rootLogger.addAppender(rollingFileAppender);

    }
}