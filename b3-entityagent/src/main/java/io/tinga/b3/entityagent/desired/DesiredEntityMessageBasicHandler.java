package io.tinga.b3.entityagent.desired;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.entityagent.operation.EntityMessage;
import io.tinga.b3.entityagent.operation.EntityOperation;
import io.tinga.b3.entityagent.operation.EntityOperationFactory;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;
import io.tinga.b3.entityagent.operation.InvalidEntityOperationException;
import io.tinga.b3.entityagent.reported.ReportedStore;
import io.tinga.b3.protocol.B3MessageValidationException;
import io.tinga.b3.protocol.B3MessageValidator;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

public class DesiredEntityMessageBasicHandler implements DesiredEntityMessageHandler {

    private final static Logger log = LoggerFactory.getLogger(DesiredEntityMessageBasicHandler.class);

    @Inject
    private EntityOperationGrantsChecker grantsChecker;

    @Inject
    private B3MessageValidator conflictsDetector;

    @Inject
    private ReportedStore reportedStore;

    @Inject
    private EntityOperationFactory operationFactory;

    @Inject
    private ITopicFactory topicFactory;

    private Map<String, Topic<EntityMessage>> topicCache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Class<EntityMessage> getEventClass() {
        return EntityMessage.class;
    }

    @Override
    public boolean handle(String topic, EntityMessage event) throws Exception {
        log.debug(String.format("operation %s", topic));
        try {
            EntityMessage current = this.reportedStore.read(topic);
            this.conflictsDetector.validateUpdate(current, event);
            EntityOperation operation = this.operationFactory.buildFrom(topic, event);
            if (this.grantsChecker.isAllowed(operation)) {
                this.getTopic(topic).post(event);
            }
        } catch (InvalidEntityOperationException e) {
            log.info(String.format("invalid operation format %s", topic));
        } catch (B3MessageValidationException e) {
            log.warn(String.format("conflict on %s: %s", topic, e.getMessage()));
        }

        return true;
    }

    private Topic<EntityMessage> getTopic(String topicName) {
        Topic<EntityMessage> topic = this.topicCache.get(topicName);
        if (topic == null) {
            topic = topicFactory.getTopic(topicName);
            this.updateTopicCache(topicName, topic);
        }

        return topic;
    }

    public void updateTopicCache(String topicName, Topic<EntityMessage> topic) {
        this.topicCache.put(topicName, topic);
    }

}
