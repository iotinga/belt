package io.tinga.b3.helpers.messageprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FromFileB3MessageProviderTest {

    private static final String BASE_MESSAGE = "{\"TS\":1751644685000,\"VER\":10,\"PROT\":1,\"CID\":\"qwerty\",\"STATUS\":\"OK\",\"BODY\":{\"KEY1\":1,\"KEY2\":\"hello world\",\"KEY3\":[0,1,2,3]}}";
    private static final InputStream INPUT_STREAM = new ByteArrayInputStream(BASE_MESSAGE.getBytes());
    private static final String TEST_PATH = "/test.json";

    @Mock
    ObjectMapper om;

    @Mock
    GenericB3Message message;

    FromFileB3MessageProvider<GenericB3Message> sut;

    @BeforeEach
    void setup() {
        sut = new FromFileB3MessageProvider<GenericB3Message>(GenericB3Message.class, om) {
            @Override
            public InputStream getMessageInputStream(String messagePath) throws IOException {
                if(TEST_PATH.equals(messagePath)) return INPUT_STREAM;
                throw new IOException();
            }
        };
    }

    @Test
    void shouldLoadMessageSuccessfully() throws Exception {
        when(om.readValue(INPUT_STREAM, GenericB3Message.class)).thenReturn(message);

        GenericB3Message result = sut.load(TEST_PATH);

        assertThat(result).isEqualTo(message);
    }

    @Test
    void shouldReturnNullOnIOException() throws Exception {
        B3Message<?> result = sut.load("/missing.json");
        assertThat(result).isNull();
    }
}
