package io.tinga.b3.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.belt.output.Status;

public class B3Message<B> {
    @JsonProperty("TS")
    private Long timestamp;

    @JsonProperty("VER")
    private Integer version;

    @JsonProperty("PROT")
    private Integer protocolVersion;

    @JsonProperty("ACTION")
    private Action action;

    @JsonProperty("STATUS")
    private Status status;

    @JsonProperty("BODY")
    private B body;

    public B3Message() {
    }

    public B3Message(Long timestamp, Integer version, Integer protocolVersion, Action action, Status status,
            B body) {
        this.timestamp = timestamp;
        this.version = version;
        this.protocolVersion = protocolVersion;
        this.action = action;
        this.status = status;
        this.body = body;
    }

    public B3Message<B> response(Long timestamp, Status status, Integer version, B body) {
        return new B3Message<B>(timestamp, version, protocolVersion, action, status, body);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getProtocolVersion() {
        return protocolVersion;
    }

    public Action getAction() {
        return action;
    }

    public Status getStatus() {
        return status;
    }

    public B getBody() {
        return body;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setProtocolVersion(Integer protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setBody(B body) {
        this.body = body;
    }

    @Override
    public String toString() {
        String bodyString = this.body == null ? "[NULL]" : "[...]";
        String version = this.version == null ? "[NULL]" : this.version.toString();
        return String.format("%d[%d]:%s - %s - v%d %s", this.timestamp,
                this.protocolVersion,
                this.action.name(),
                this.status.name(),
                version,
                bodyString);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof B3Message)) {
            return false;
        }

        B3Message<?> otherMessage = (B3Message<?>) other;

        if (this == otherMessage) {
            return true;
        }

        if (!this.getAction().equals(otherMessage.getAction())) {
            return false;
        }

        if (!this.getStatus().equals(otherMessage.getStatus())) {
            return false;
        }

        if (this.getTimestamp() == null) {
            return otherMessage.getTimestamp() == null;
        }
        if (!this.getTimestamp().equals(otherMessage.getTimestamp())) {
            return false;
        }

        if (this.getProtocolVersion() == null) {
            return otherMessage.getProtocolVersion() == null;
        }
        if (!this.getProtocolVersion().equals(otherMessage.getProtocolVersion())) {
            return false;
        }

        if (this.getVersion() == null) {
            return otherMessage.getVersion() == null;
        }
        if (!this.getVersion().equals(otherMessage.getVersion())) {
            return false;
        }

        if (this.getBody() == null) {
            return otherMessage.getBody() == null;
        }
        return this.getBody().equals(otherMessage.getBody());
    }
}