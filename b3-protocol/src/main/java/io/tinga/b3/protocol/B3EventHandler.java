package io.tinga.b3.protocol;

public interface B3EventHandler<E extends B3Message<?>> {
    public String getName();

    public boolean handle(B3Topic topic, E event) throws Exception;
}
