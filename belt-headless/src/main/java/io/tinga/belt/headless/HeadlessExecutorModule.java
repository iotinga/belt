package io.tinga.belt.headless;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.inject.AbstractModule;

import io.tinga.belt.input.GadgetCommandExecutor;

class HeadlessExecutorModule extends AbstractModule {
    private final HeadlessCommand command;
    private final ExecutorService service;

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private int count = 1;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(String.format("%d#%s", count++, r));
            return thread;
        }
    };

    public HeadlessExecutorModule(HeadlessCommand command) {
        this.command = command;
        this.service = switch (command.action()) {
            case HeadlessAction.SEQUENTIAL -> Executors.newSingleThreadExecutor();
            case HeadlessAction.CONCURRENT -> Executors.newThreadPerTaskExecutor(THREAD_FACTORY);
        };
    }

    @Override
    protected void configure() {
        bind(ExecutorService.class).toInstance(service);
        bind(HeadlessCommand.class).toInstance(command);
        bind(GadgetCommandExecutor.class).to(HeadlessCommandExecutor.class);
    }
}
