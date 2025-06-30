package io.tinga.b3.entityagent.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.entityagent.EntityConfig;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageHandler;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

public class EntityTopicOperationDaemon implements EntityOperationDaemon {

    private static final Logger log = LoggerFactory.getLogger(EntityTopicOperationDaemon.class);

    @Inject
    private DesiredEntityMessageHandler eventHandler;

    @Inject
    private EntityConfig config;

    @Inject
    private ITopicFactory topicFactory;

    public void run() {
        Topic<EntityMessage> desiredTopic = topicFactory.getTopic(this.config.getDesiredTopicFilter());
        desiredTopic.addHandler(eventHandler);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    log.debug("Interrupt: %s", e.getMessage());
                }
            }
        } finally {
            log.info("Shutdown");
        }
    }
}
