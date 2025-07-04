package io.tinga.b3.helpers.jsonschema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import io.tinga.b3.helpers.JsonSchemaProvider.Config;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JsonSchemaFromFileProviderTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();

    private static final String BASE_JSON_SCHEMA = "{}";
    private static final String VALID_TEST_TOPIC = "b3/agent/test1/shadow/reported";
    private static final InputStream INPUT_STREAM = new ByteArrayInputStream(BASE_JSON_SCHEMA.getBytes());

    private final B3Topic testTopic = topicFactory.parse(VALID_TEST_TOPIC).build();


    @Mock
    Config config;

    @Mock
    ObjectMapper om;

    @Mock
    JsonSchemaFactory factory;

    @Mock
    JsonSchema jsonSchema;

    @Mock
    JsonNode jsonNode;

    JsonSchemaFromFileProvider sut;
    JsonSchemaFromFileProvider sutWithException;



    @BeforeEach
    void setup() {
        sut = new JsonSchemaFromFileProvider(config, om, factory) {
            @Override
            public InputStream getSchemaInputStream(String topic) throws IOException {
                return INPUT_STREAM;
            }
        };
        sutWithException = new JsonSchemaFromFileProvider(config, om, factory) {
            @Override
            public InputStream getSchemaInputStream(String topic) throws IOException {
                throw new IOException();
            }
        };
    }

@Test
    void shouldReturnSchemaFromCacheWhenEnabled() {
        when(config.isJsonSchemaCacheEnabled()).thenReturn(true);
        sut.updateCache(testTopic, jsonSchema);

        JsonSchema result = sut.getSchemaFor(testTopic);

        assertThat(result).isEqualTo(jsonSchema);
        verifyNoInteractions(om, factory);
    }

    @Test
    void shouldLoadSchemaFromFileAndCacheItWhenCacheEnabled() throws Exception {
        when(config.isJsonSchemaCacheEnabled()).thenReturn(true);

        JsonNode parsedNode = new ObjectMapper().readTree(BASE_JSON_SCHEMA);

        when(om.readTree(INPUT_STREAM)).thenReturn(parsedNode);
        when(factory.getSchema(parsedNode)).thenReturn(jsonSchema);

        JsonSchema result = sut.getSchemaFor(testTopic);

        assertThat(result).isEqualTo(jsonSchema);
        assertThat(sut.cache).containsEntry(testTopic, jsonSchema);
    }

    @Test
    void shouldLoadSchemaWithoutCachingWhenCacheDisabled() throws Exception {
        when(config.isJsonSchemaCacheEnabled()).thenReturn(false);

        JsonNode parsedNode = new ObjectMapper().readTree(BASE_JSON_SCHEMA);

        when(om.readTree(INPUT_STREAM)).thenReturn(parsedNode);
        when(factory.getSchema(parsedNode)).thenReturn(jsonSchema);

        JsonSchema result = sut.getSchemaFor(testTopic);

        assertThat(result).isEqualTo(jsonSchema);
        assertThat(sut.cache).doesNotContainKey(testTopic);
    }

    @Test
    void shouldReturnCorrectSchemaFilePath() {
        when(config.getJsonSchemaBasePath()).thenReturn("schemas");

        String path = sut.getSchemaPath(testTopic);

        assertThat(path).isEqualTo("schemas/" + VALID_TEST_TOPIC + ".json");
    }

    @Test
    void shouldReturnNullOnIOException() throws Exception {
        when(config.isJsonSchemaCacheEnabled()).thenReturn(false);

        JsonSchema result = sutWithException.getSchemaFor(testTopic);

        assertEquals(null, result);
        assertThat(sut.cache).doesNotContainKey(testTopic);
    }
}
