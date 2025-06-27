package io.tinga.b3.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import io.tinga.belt.output.Status;

public class GenericMessage extends RawMessage<JsonNode> {
    public GenericMessage(Long timestamp, Integer version, Integer protocolVersion, Action action, Status status,
                        JsonNode body) {
        super(timestamp, version, protocolVersion, action, status, body);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof GenericMessage)) {
            return false;
        }

        GenericMessage otherMessage = (GenericMessage) other;

        if (this == otherMessage) {
            return true;
        }

        if(!this.getAction().equals(otherMessage.getAction())) {
            return false;
        }

        if(!this.getStatus().equals(otherMessage.getStatus())) {
            return false;
        }

        if(this.getTimestamp() == null) {
            return otherMessage.getTimestamp() == null;
        }
        if(!this.getTimestamp().equals(otherMessage.getTimestamp())) {
            return false;
        }

        if(this.getProtocolVersion() == null) {
            return otherMessage.getProtocolVersion() == null;
        }
        if(!this.getProtocolVersion().equals(otherMessage.getProtocolVersion())) {
            return false;
        }

        if(this.getVersion() == null) {
            return otherMessage.getVersion() == null;
        }
        if(!this.getVersion().equals(otherMessage.getVersion())) {
            return false;
        }

        if(this.getBody() == null) {
            return otherMessage.getBody() == null;
        }
        return this.getBody().equals(otherMessage.getBody());
    }
}
