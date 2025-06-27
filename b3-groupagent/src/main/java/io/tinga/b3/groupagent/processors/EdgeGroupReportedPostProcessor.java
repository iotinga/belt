package io.tinga.b3.groupagent.processors;

import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.tinga.b3.core.shadowing.ShadowReportedPostProcessor;
import io.tinga.b3.groupagent.GroupAgentCommand;
import io.tinga.b3.protocol.GenericMessage;

public class EdgeGroupReportedPostProcessor implements ShadowReportedPostProcessor<GenericMessage> {

    public static final String FRAGS_PROPERTY_NAME = "frags";
    public static final String FRAGS_COUNT_PROPERTY_NAME = "fragsCount";

    private final GroupAgentCommand command;
    private final ObjectMapper om;

    @Inject
    public EdgeGroupReportedPostProcessor(GroupAgentCommand command, ObjectMapper om) {
        this.command = command;
        this.om = om;
    }

    @Override
    public void initialize() {
        // NOTHING TO DO
    }

    @Override
    public void inPlaceProcess(GenericMessage outcomingReported) {
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
        shadow.set(this.command.agentId(), agent);
    }

}
