package io.tinga.b3.agent.driver.processors;

import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.tinga.b3.agent.GroupAgentConfig;
import io.tinga.b3.agent.driver.ShadowReportedPostProcessor;
import io.tinga.b3.protocol.impl.GenericB3Message;

public class EdgeGroupAgentReportedPostProcessor implements ShadowReportedPostProcessor<GenericB3Message> {

    public static final String FRAGS_PROPERTY_NAME = "frags";
    public static final String FRAGS_COUNT_PROPERTY_NAME = "fragsCount";

    private final GroupAgentConfig config;
    private final ObjectMapper om;

    @Inject
    public EdgeGroupAgentReportedPostProcessor(GroupAgentConfig config, ObjectMapper om) {
        this.config = config;
        this.om = om;
    }

    @Override
    public void initialize() {
        // NOTHING TO DO
    }

    @Override
    public void inPlaceProcess(GenericB3Message outcomingReported) {
        if (outcomingReported == null)
            return;

        ObjectNode shadow = (ObjectNode) outcomingReported.getBody();
        ObjectNode agent = this.om.createObjectNode();
        ArrayNode fragments = this.om.createArrayNode();

        Iterator<String> keys = shadow.fieldNames();
        int count = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            fragments.add(key);
            count++;
        }

        agent.set(FRAGS_PROPERTY_NAME, fragments);
        agent.put(FRAGS_COUNT_PROPERTY_NAME, count);
        shadow.set(this.config.agentId(), agent);
    }

}
