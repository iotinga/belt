package io.tinga.b3.helpers.messageprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.Topic;

public class FromTopicMessageProvider<M extends B3Message<?>> implements B3MessageProvider<M>, EventHandler<M> {

    private final static Logger log = LoggerFactory.getLogger(FromTopicMessageProvider.class);

    protected final Class<M> messageClass;
    protected final B3ITopicFactoryProxy topicFactoryProxy;
    protected final B3Topic.Factory topicFactory;

    private M loadedMessage;
    private B3Topic activeB3Topic;
    private Topic<M> topic;

    @Inject
    public FromTopicMessageProvider(Class<M> messageClass, B3ITopicFactoryProxy topicFactoryProxy,
            B3Topic.Factory topicFactory) {
        this.messageClass = messageClass;
        this.topicFactoryProxy = topicFactoryProxy;
        this.topicFactory = topicFactory;
    }

    @Override
    public synchronized M load(String messagePath) {
        this.loadedMessage = null;
        this.activeB3Topic = topicFactory.parse(messagePath).build();
        this.topic = topicFactoryProxy.getTopic(activeB3Topic, false);
        topic.addHandler(this);
        M retval = null;
        while ((retval = this.getLoadedMessage()) == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
        this.activeB3Topic = null;
        return retval;
    }

    @Override
    public String getName() {
        return FromTopicMessageProvider.class.getSimpleName();
    }

    @Override
    public Class<M> getEventClass() {
        return this.messageClass;
    }

    protected M getLoadedMessage() {
        return this.loadedMessage;
    }

    protected B3Topic getActiveB3Topic() {
        return this.activeB3Topic;
    }

    @Override
    public boolean handle(String topicPath, M event) throws Exception {
        B3Topic topic = topicFactory.parse(topicPath).build();
        B3Topic activeB3Topic = this.getActiveB3Topic();

        if(activeB3Topic != null && activeB3Topic.equals(topic)) {
            this.loadedMessage = event;
        } else {
            String activeTopicPath = activeB3Topic == null ? "[NULL]" : activeB3Topic.toString();
            log.warn("Received event not related to the current active topic. Received=%s, Active=%s", topicPath, activeTopicPath);
        }

        return true;
    }

}
