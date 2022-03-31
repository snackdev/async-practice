package com.example.asyncpractice.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncLogic {
    @Async
    public String someComplexCaculation(int durationSeconds) {
        log.info("calculation start In Thread : {}............", Thread.currentThread().getName());
        try {
            Thread.sleep(durationSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            String result = String.valueOf(Math.random());
            log.info("calculation end with {} ............", result);
            return result;
        }
    }

}
