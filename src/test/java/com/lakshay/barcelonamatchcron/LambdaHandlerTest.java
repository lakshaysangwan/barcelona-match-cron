package com.lakshay.barcelonamatchcron;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.Test;
import org.mockito.Mockito;

public class LambdaHandlerTest {

    @Test
    public void testLocalLambdaExecution() {
        // Create the handler
        LambdaHandler handler = new LambdaHandler();

        // Mock AWS Lambda Context
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getFunctionName()).thenReturn("localTestFunction");
        Mockito.when(context.getRemainingTimeInMillis()).thenReturn(300000); // 5 minutes

        // Create a sample input object if needed
        Object input = new Object(); // Modify this based on your actual input type

        // Execute the handler
        String result = handler.handleRequest(input, context);

        // Print the result
        System.out.println("Lambda execution result: " + result);
    }
}