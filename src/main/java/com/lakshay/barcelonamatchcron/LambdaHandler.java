package com.lakshay.barcelonamatchcron;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.lakshay.barcelonamatchcron.service.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class LambdaHandler implements RequestHandler<Object, String> {
    private static ConfigurableApplicationContext applicationContext;

    @Override
    public String handleRequest(Object input, Context context) {
        if (applicationContext == null) {
            applicationContext = SpringApplication.run(BarcelonaMatchCronApplication.class);
        }

        try {
            MainService mainService = applicationContext.getBean(MainService.class);
            mainService.runTask();
            return "Task executed successfully";
        } catch (Exception e) {
            return "Error executing task: " + e.getMessage();
        }
    }
}