package com.example.asyncpractice.rest;

import com.example.asyncpractice.logic.CommonLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/common")
public class CommonResource {
    private final CommonLogic commonLogic;

    @GetMapping("/sync/{duration}")
    public String doSync(@PathVariable("duration") int duration) {
        return commonLogic.doSync(duration);
    }

    @GetMapping("/async/{asyncType}/{duration}")
    public String doAsync(@PathVariable("asyncType") int asyncType, @PathVariable("duration") int duration) throws ExecutionException, InterruptedException {
        return commonLogic.doAsync(asyncType, duration);
    }
}
