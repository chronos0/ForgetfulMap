package com.origami;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForgetfulMapTest {

    public static final int MAXIMUM_ENTRIES = 3;
    ForgetfulMap forgetfulMap;

    @BeforeEach
    void setUp() {
        forgetfulMap = new ForgetfulMap(MAXIMUM_ENTRIES);
    }

    @Test
    @DisplayName("Test that a value is correctly added to the content, contentData maps")
    void add() {

        forgetfulMap.add("key1", "value1");

        assertAll(
                () -> assertTrue(forgetfulMap.getContent().containsKey("key1"), "key not found"),
                () -> assertTrue(forgetfulMap.getContent().containsValue("value1"), "value not found"),
                () -> assertTrue(forgetfulMap.getContent().size() == 1, "content size incorrect"),
                () -> assertTrue(forgetfulMap.getContentData().containsKey("key1"), "content data key not found"),
                () -> assertTrue(forgetfulMap.getContentDataEntry("key1").getAccessCount() == 0, "content data count not 0"),
                () -> assertEquals(forgetfulMap.getContentDataEntry("key1").getLastAccessed().truncatedTo(SECONDS), LocalDateTime.now().truncatedTo(SECONDS), "datetime incorrect"),
                () -> assertTrue(forgetfulMap.getContentData().size() == 1, "content data size incorrect")
        );
    }


    @Test
    @DisplayName("Test map accepts doubles and ints")
    void add_withDifferentDataTypes() {

        forgetfulMap.add(1, 2.0);

        assertAll(
                () -> assertTrue(forgetfulMap.getContent().containsKey(1), "key not found"),
                () -> assertTrue(forgetfulMap.getContent().containsValue(2.0), "value not found"),
                () -> assertTrue(forgetfulMap.getContent().size() == 1, "content size incorrect"),
                () -> assertTrue(forgetfulMap.getContentData().containsKey(1), "content data key not found"),
                () -> assertTrue(forgetfulMap.getContentDataEntry(1).getAccessCount() == 0, "content data count not 0"),
                () -> assertEquals(forgetfulMap.getContentDataEntry(1).getLastAccessed().truncatedTo(SECONDS), LocalDateTime.now().truncatedTo(SECONDS), "datetime incorrect"),
                () -> assertTrue(forgetfulMap.getContentData().size() == 1, "content data size incorrect")
        );


    }

    @Test
    @DisplayName("Oldest/last used entry is removed for 0 retrievals, when entry is added to full map")
    void addAtCapacityLimit() {

        forgetfulMap.add("key1", "value1");
        forgetfulMap.add("key2", "value2");
        forgetfulMap.add("key3", "value3");
        forgetfulMap.add("key4", "value4");

        assertAll(
                () -> assertFalse(forgetfulMap.getContent().containsKey("key1"), "key found but not expected"),
                () -> assertNull(forgetfulMap.getContentDataEntry("key1"), "key found but not expected"),
                () -> assertTrue(forgetfulMap.getContent().size() == 3, "content size incorrect")
        );
    }

    @Test
    @DisplayName("Least used entry is removed for >0 retrievals, when entry is added to full map")
    void addAtCapacityLimit_leastUsedRemoved() {

        forgetfulMap.add("key1", "value1");
        forgetfulMap.add("key2", "value2");
        forgetfulMap.add("key3", "value3");

        forgetfulMap.find("key1");
        forgetfulMap.find("key1");
        forgetfulMap.find("key1");
        forgetfulMap.find("key2");
        forgetfulMap.find("key2");
        forgetfulMap.find("key3");

        forgetfulMap.add("key4", "value4");

        assertAll(
                () -> assertFalse(forgetfulMap.getContent().containsKey("key3"), "key found but not expected"),
                () -> assertNull(forgetfulMap.getContentDataEntry("key3"), "key found but not expected"),
                () -> assertTrue(forgetfulMap.getContent().size() == 3, "content size incorrect")
        );
    }

    @Test
    @DisplayName("Test that the correct value is found, and the metadata updated")
    void find() {

        forgetfulMap.add("key1", "value1");

        assertAll(
                () -> assertEquals("value1", forgetfulMap.find("key1"), "key not found"),
                () -> assertNull(forgetfulMap.find("key2"), "key found"),
                () -> assertEquals(1, forgetfulMap.getContentDataEntry("key1").getAccessCount(), "metadata not updated"),
                () -> assertEquals(LocalDateTime.now().truncatedTo(SECONDS), forgetfulMap.getContentDataEntry("key1").getLastAccessed().truncatedTo(SECONDS), "datetime incorrect")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, Integer.MAX_VALUE + 1})
    @DisplayName("exception is thrown for invalid params")
    void invalidParamsThroughException(int max) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ForgetfulMap<>(max));
        assertEquals("maximum entries must be between 1 and Integer Max Value", exception.getMessage());

    }

    @Test
    @DisplayName("exception is thrown when maximum entries is null")
    void invalidParam_null() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ForgetfulMap<>(null));
        assertEquals("maximum entries must be between 1 and Integer Max Value", exception.getMessage());
    }


    @Test
    @DisplayName("entries are deleted from content and content data")
    void delete() {

        forgetfulMap.add("key1", "value1");
        forgetfulMap.delete("key1");

        assertAll(
                () -> assertFalse(forgetfulMap.getContent().containsKey("key1"), "key found"),
                () -> assertFalse(forgetfulMap.getContentData().containsKey("key1"), "key found"),
                () -> assertTrue(forgetfulMap.getContent().size() == 0, "size > 0"),
                () -> assertTrue(forgetfulMap.getContentData().size() == 0, "data size > 0"),
                () -> assertNull(forgetfulMap.find("key1"))
        );
    }

    @Test
    @DisplayName("Entries are updated without updating content data")
    void update() {

        forgetfulMap.add("key1", "value1");
        forgetfulMap.update("key1", "value2");
        forgetfulMap.update("key2", "value3");

        assertAll(
                () -> assertEquals("value2", forgetfulMap.getContent().get("key1"), "value incorrect"),
                () -> assertEquals("value3", forgetfulMap.getContent().get("key2"), "value incorrect"),
                () -> assertTrue(forgetfulMap.getContent().size() == 2, "size != 2"),
                () -> assertTrue(forgetfulMap.getContentData().size() == 2, "size != 2"),
                () -> assertTrue(forgetfulMap.getContentDataEntry("key1").getAccessCount() == 0, "count != 0"),
                () -> assertTrue(forgetfulMap.getContentDataEntry("key2").getAccessCount() == 0, "count != 0")
        );
    }

}
