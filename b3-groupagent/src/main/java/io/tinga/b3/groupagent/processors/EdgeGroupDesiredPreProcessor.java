package io.tinga.b3.groupagent.processors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.tinga.b3.core.shadowing.ShadowDesiredPreProcessor;
import io.tinga.b3.groupagent.GroupAgentCommand;
import io.tinga.b3.protocol.GenericMessage;

public class EdgeGroupDesiredPreProcessor implements ShadowDesiredPreProcessor<GenericMessage> {

    private final GroupAgentCommand command;

    @Inject
    public EdgeGroupDesiredPreProcessor(GroupAgentCommand command) {
        this.command = command;
    }

    @Override
    public void inPlaceProcess(GenericMessage incomingDesired) {
        if (incomingDesired == null)
            return;

        ObjectNode shadow = (ObjectNode) incomingDesired.getBody();
        shadow.remove(this.command.agentId());
    }

    @Override
    public void initialize() {
        // NOTHING TO DO
    }

}
