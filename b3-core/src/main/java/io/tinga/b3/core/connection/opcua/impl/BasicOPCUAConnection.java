package io.tinga.b3.core.connection.opcua.impl;

import java.io.IOException;
import java.util.List;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;

import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tinga.b3.core.connection.ConnectionState;
import io.tinga.b3.core.connection.opcua.OPCUAConnection;
import io.tinga.b3.core.connection.opcua.OPCUAType;

public class BasicOPCUAConnection implements OPCUAConnection {

    private static final Logger log = LoggerFactory.getLogger(BasicOPCUAConnection.class);

    private final String endpoint;
    private final String username;
    private final String password;
    private final String host;
    private OpcUaClient client;
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;

    public BasicOPCUAConnection(String schema, String host, Integer port, String username, String password) {
        this.endpoint = this.getEndpoint(schema, host, port);
        this.username = username;
        this.password = password;
        this.host = host;
    }

    private String getEndpoint(String schema, String host, Integer port) {
        return String.format("%s://%s:%d", schema, host, port);
    }

    @Override
    public void connect() {
        if (client == null) {
            try {
                List<EndpointDescription> endpoints;

                try {
                    endpoints = DiscoveryClient.getEndpoints(this.endpoint).get();
                } catch (Throwable e) {
                    // try the expliecit discovery endpoint as well
                    String discoveryUrl = this.endpoint;
                    if (!discoveryUrl.endsWith("/")) {
                        discoveryUrl += "/";
                    }
                    discoveryUrl += "discovery";

                    log.info("Trying explicit discovery URL: {}", discoveryUrl);
                    endpoints = DiscoveryClient.getEndpoints(discoveryUrl).get();
                }

                log.info("Discovered OPCUA endpoints: {}", endpoints);

                OpcUaClientConfigBuilder configBuilder = OpcUaClientConfig.builder()
                        .setApplicationName(LocalizedText.english("BRAID OPC UA Client"))
                        .setEndpoint(EndpointUtil.updateUrl(endpoints.getFirst(), this.host));

                if (this.username != null && this.password != null) {
                    log.info("Creating OPC UA client with credentials");
                    UsernameProvider usernameProvider = new UsernameProvider(this.username, this.password);
                    configBuilder.setIdentityProvider(usernameProvider);
                }

                OpcUaClientConfig config = configBuilder.build();
                client = OpcUaClient.create(config);
                client.connect().get();
                connectionState = ConnectionState.CONNECTED;
            } catch (Exception exception) {
                log.error("OPC UA client connection failed with exeption: {}", exception.getMessage());
                // throw new IOException(exception.getMessage(), exception);
            }
        }
    }

    @Override
    public void disconnect() {
        if (client != null) {
            try {
                client.disconnect().get();
                connectionState = ConnectionState.DISCONNECTED;
            } catch (Exception exception) {
                log.error("OPCUA tear down failed");
                // throw new IOException(exception.getMessage(), exception);
            }
        }
    }

    @Override
    public ConnectionState getConnectionState() {
        return connectionState;
    }

    @Override
    public Object readVariable(int namespace, String identifier) throws Exception {
        this.connect();

        NodeId nodeId = new NodeId(namespace, identifier);
        DataValue data = null;

        try {
            data = client.getAddressSpace()
                    .getVariableNode(nodeId)
                    .readValue();
        } catch (Exception exception) {
            throw new IOException(exception.getMessage(), exception);
        }

        return data.getValue().getValue();
    }

    @Override
    public void writeVariable(int namespace, String identifier, Object value) throws Exception {
        this.connect();

        NodeId nodeId = new NodeId(namespace, identifier);

        try {
            Variant variant = new Variant(value);
            DataValue data = new DataValue(variant, null, null);
            StatusCode statusCode = client.writeValue(nodeId, data).get();
            if (statusCode.isBad()) {
                log.error("failed to write n={};s={} with status code {}", namespace, identifier, statusCode);
                throw new Exception("write failed with bad status code = " + statusCode);
            }
        } catch (Exception exception) {
            throw new IOException(exception.getMessage(), exception);
        }
    }

    public void writeVariable(int namespace, String identifier, Object value, OPCUAType type) throws Exception {
        switch (type) {
            case UINT16 -> writeVariable(namespace, identifier, Unsigned.ushort((Integer) value));
            case UINT32 -> writeVariable(namespace, identifier, Unsigned.uint((Integer) value));
            default -> writeVariable(namespace, identifier, value);
        }
    }

    public Short readShort(int namespace, String identifier) throws Exception {
        try {
            return ((Short) readVariable(namespace, identifier));
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read Short for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public Integer readUInt16(int namespace, String identifier) throws Exception {
        try {
            return ((UShort) readVariable(namespace, identifier)).intValue();
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read UShort for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public Long readUInteger(int namespace, String identifier) throws Exception {
        try {
            return ((UInteger) readVariable(namespace, identifier)).longValue();
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read UInteger for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public Float readFloat(int namespace, String identifier) throws Exception {
        try {
            return ((Float) readVariable(namespace, identifier));
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read Float for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public Double readDouble(int namespace, String identifier) throws Exception {
        try {
            return ((Double) readVariable(namespace, identifier));
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read Double for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public Boolean readBoolean(int namespace, String identifier) throws Exception {
        try {
            return ((Boolean) readVariable(namespace, identifier));
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read Boolean for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public Boolean[] readBooleanArray(int namespace, String identifier) throws Exception {
        try {
            return ((Boolean[]) readVariable(namespace, identifier));
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read Boolean Array for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public String readString(int namespace, String identifier) throws Exception {
        try {
            return ((String) readVariable(namespace, identifier));
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read String for n={};s={}", namespace, identifier);
            return null;
        }
    }

    public Byte readByte(int namespace, String identifier) throws Exception {
        try {
            return ((Byte) readVariable(namespace, identifier));
        } catch (ClassCastException | NumberFormatException e) {
            log.error("Could not read Byte for n={};s={}", namespace, identifier);
            return null;
        }
    }


    public void writeUShort(int namespace, String identifier, Integer value) throws Exception, NumberFormatException {
        UShort uInt16Value = UShort.valueOf(value);
        writeVariable(namespace, identifier, uInt16Value);
    }
}
