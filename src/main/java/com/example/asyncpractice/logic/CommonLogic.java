package com.example.asyncpractice.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommonLogic {
    //
    private final AsyncLogic asyncLogic;

    public String doSync(int durationSeconds) {
        log.info("doSync start ............");
        String result = someComplexCaculation(durationSeconds);
        log.info("doSync end ............");
        return result;
    }

    public String doAsync(int asyncType, int durationSeconds) throws ExecutionException, InterruptedException {
        switch (asyncType) {
            case 1:
                // 동작은 잘 하나, 관리되지 않는 스레드로 생성 (Thread : Thread-7)
                return manualThread(durationSeconds);
            case 2:
                // get 함수에 의해 http 요청 lock, 관리되지 않는 스레드로 생성 (Thread : ForkJoinPool.commonPool-worker-19)
                return compFutureWithGet(durationSeconds);
            case 3:
                // 내부 호출 메서드는 aop 프록시 미적용되어 동기 처리됨
                return innerAsyncAnnotation(durationSeconds);
            case 4:
                // async 처리 되나 @Async에 의한 처리가 아님, 관리되지 않는 스레드로 생성 (Thread : ForkJoinPool.commonPool-worker-19)
                return compFutureWithInnerAsyncAnnotation(durationSeconds);
            case 5:
                // @Async 하나만으로 처리됨, 관리되는 스레드로 생성 (Thread : ManagedByTaskExecutor-1)
                return compFutureWithExternalAsyncAnnotation(durationSeconds);
            default:
                throw new IllegalArgumentException("asyncType not valid");
        }
    }

    public String manualThread(int durationSeconds) {
        log.info("manualThread start ............ {}", Thread.currentThread().getName());
        Thread thread = new Thread(() -> someComplexCaculation(durationSeconds));
        String result = thread.getName();
        thread.start();
        log.info("manualThread end ............ {}", Thread.currentThread().getName());
        return result;
    }

    public String compFutureWithGet(int durationSeconds) throws ExecutionException, InterruptedException {
        log.info("compFutureWithGet start ............ {}", Thread.currentThread().getName());
        CompletableFuture future = CompletableFuture.supplyAsync(() -> someComplexCaculation(durationSeconds));
        String result = (String) future.get();
        log.info("compFutureWithGet end ............");
        return result;
    }

    public String innerAsyncAnnotation(int durationSeconds) {
        log.info("innerAsyncAnnotation start ............ {}", Thread.currentThread().getName());
        someComplexCaculation(durationSeconds);
        log.info("innerAsyncAnnotation end ............");
        return "OK";
    }

    public String compFutureWithInnerAsyncAnnotation(int durationSeconds) {
        log.info("compFutureWithInnerAsyncAnnotation start ............ {}", Thread.currentThread().getName());
        wrapToAsync(sec -> someComplexCaculation(sec), durationSeconds);
        log.info("compFutureWithInnerAsyncAnnotation end ............");
        return "OK";
    }


    public String compFutureWithExternalAsyncAnnotation(int durationSeconds) {
        log.info("compFutureWithExternalAsyncAnnotation start ............ {}", Thread.currentThread().getName());
        asyncLogic.someComplexCaculation(durationSeconds);
        log.info("compFutureWithExternalAsyncAnnotation end ............");
        return "OK";
    }

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

    @Async
    public <R, T> CompletableFuture<R> wrapToAsync(Function<T, R> task, T arg) {
        CompletableFuture future = CompletableFuture.supplyAsync(() -> task.apply(arg));
        return future;
    }
}
