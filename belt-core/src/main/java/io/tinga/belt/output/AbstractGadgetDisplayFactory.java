package io.tinga.belt.output;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import io.tinga.belt.GadgetContext;


public abstract class AbstractGadgetDisplayFactory implements GadgetDisplayFactory {

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private int count = 1;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(String.format("%d#%s", count++, r));
            return thread;
        }
    };

    private final ExecutorService displayExecutor;

    public AbstractGadgetDisplayFactory() {
        this.displayExecutor = Executors.newThreadPerTaskExecutor(THREAD_FACTORY);
    }

    @Override
    public <C> Future<?> buildDisplay(GadgetContext<C> context) {
        return this.displayExecutor.submit(new GadgetSlf4jDisplay(context.output()));
    }
    
    public abstract <C> GadgetDisplay buildDisplayInstance(GadgetContext<C> context);
}
