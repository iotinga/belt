package io.tinga.belt.testgadget;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;

public class TestGadgetExecutor implements GadgetCommandExecutor {
    Logger log = LoggerFactory.getLogger(TestGadgetExecutor.class);

    @Override
    public CompletableFuture<Status> submit(Object command) {
        while (true) {
            try {
                log.info("Hello");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return new CompletableFuture<>();
            }
        }
    }

}
