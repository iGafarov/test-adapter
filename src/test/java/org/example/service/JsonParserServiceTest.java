package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.configuration.MappingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

@SpringBootTest/*(classes = Application.class)*/
class JsonParserServiceTest {
    @Autowired
    private JsonParserService jsonParserService;

    @Autowired
    private MappingConfig mappingConfig;

    @Test
    void testJsonParsingWithMappingConfig() throws Exception {
        String json = "{"
                + "\"messageType\": \"REGISTER_POSITION\","
                + "\"header\": {"
                + "\"accountingDate\": \"2024-09-10\","
                + "\"productId\": \"test1\","
                + "\"messageId\": \"2fc008a2-0000\""
                + "},"
                + "\"dfaRegisterPosition\": {"
                + "\"registers\": ["
                + "{ \"registerType\": \"TEST3\", \"restIn\": 1000000 },"
                + "{ \"registerType\": \"TEST4\", \"restIn\": 50000 }"
                + "]"
                + "}"
                + "}";

        assertNotNull(mappingConfig);
        assertFalse(mappingConfig.getFields().isEmpty());
        assertFalse(mappingConfig.getArrayFields().isEmpty());

        Map<String, Object> result = jsonParserService.parseJson(json);

        assertEquals("test1", result.get("productId"));
        assertEquals("2fc008a2-0000", result.get("messageId"));
        assertEquals("2024-09-10", result.get("accountingDate"));

        Map<String, String> test3Data = (Map<String, String>) result.get("TEST3");
        Map<String, String> test4Data = (Map<String, String>) result.get("TEST4");

        assertEquals("TEST3", test3Data.get("registerType"));
        assertEquals("1000000", test3Data.get("restIn"));

        assertEquals("TEST4", test4Data.get("registerType"));
        assertEquals("50000", test4Data.get("restIn"));
    }

    @Test
    void testJsonParsingWithAddedFieldToMapping() throws Exception {
        String json = "{"
                + "\"messageType\": \"REGISTER_POSITION\","
                + "\"header\": {"
                + "\"accountingDate\": \"2024-09-10\","
                + "\"productId\": \"test1\","
                + "\"messageId\": \"2fc008a2-0000\""
                + "},"
                + "\"dfaRegisterPosition\": {"
                + "\"registers\": ["
                + "{ \"registerType\": \"TEST3\", \"restIn\": 1000000 },"
                + "{ \"registerType\": \"TEST4\", \"restIn\": 50000, \"addedField\": \"added\" }"
                + "]"
                + "}"
                + "}";

        assertNotNull(mappingConfig);
        assertFalse(mappingConfig.getFields().isEmpty());
        assertFalse(mappingConfig.getArrayFields().isEmpty());

        mappingConfig.getArrayFields().put("addedField", "dfaRegisterPosition.registers.addedField");

        Map<String, Object> result = jsonParserService.parseJson(json);

        Map<String, String> test4Data = (Map<String, String>) result.get("TEST4");

        assertEquals("TEST4", test4Data.get("registerType"));
        assertEquals("50000", test4Data.get("restIn"));
        assertEquals("added", test4Data.get("addedField"));
    }
}

