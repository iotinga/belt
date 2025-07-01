package io.tinga.b3.core.driver.connection.opcua;

import io.tinga.b3.core.driver.Connection;

public interface OPCUAConnection extends Connection {
    Object readVariable(int namespace, String identifier) throws Exception;

    Short readShort(int namespace, String identifier) throws Exception;

    Integer readUInt16(int namespace, String identifier) throws Exception;

    Long readUInteger(int namespace, String identifier) throws Exception;

    Float readFloat(int namespace, String identifier) throws Exception;

    Double readDouble(int namespace, String identifier) throws Exception;

    Boolean readBoolean(int namespace, String identifier) throws Exception;

    Boolean[] readBooleanArray(int namespace, String identifier) throws Exception;

    String readString(int namespace, String identifier) throws Exception;

    Byte readByte(int namespace, String identifier) throws Exception;

    void writeVariable(int namespace, String identifier, Object value) throws Exception;

    void writeVariable(int namespace, String identifier, Object value, OPCUAType type) throws Exception;

    void writeUShort(int namespace, String identifier, Integer value) throws Exception;
}
