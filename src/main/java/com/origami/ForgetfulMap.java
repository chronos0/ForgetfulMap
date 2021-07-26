package com.origami;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ForgetfulMap<K, V> {

    Map<K, V> content;
    Map<K, MetaData> contentData;
    Integer maximumEntries;

    public ForgetfulMap(Integer maximumEntries) {
        this.maximumEntries = maximumEntries;
        assertValidParams(maximumEntries);
        content = new ConcurrentHashMap<>();
        contentData = new ConcurrentHashMap<>();
    }

    private void assertValidParams(Integer maximumEntries) {
        if (maximumEntries == null || maximumEntries < 1 || maximumEntries > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maximum entries must be between 1 and Integer Max Value");
        }
    }

    public synchronized void add(K key, V value) {

        if (content.size() == maximumEntries) {
            removeLeastUsedEntry();
        }

        content.put(key, value);
        contentData.put(key, new MetaData());

        log.info("Entry added: {}, {}", key, value);
    }

    public synchronized V find(K key) {

        MetaData metaData = contentData.get(key);

        if (metaData != null) {
            updateMetaData(metaData);
        }
        return content.get(key);
    }

    public synchronized void delete(K key) {

        content.remove(key);
        contentData.remove(key);
        log.info("Entry deleted");
    }

    public synchronized void update(K key, V value) {
        if (!content.containsKey(key)) {
            contentData.put(key, new MetaData());
        }
        content.put(key, value);
        log.info("Entry updated");
    }

    private void updateMetaData(MetaData entry) {

        entry.setLastAccessed(LocalDateTime.now());
        entry.setAccessCount(entry.getAccessCount() + 1);

    }

    private void removeLeastUsedEntry() {

        Map.Entry<K, MetaData> leastUsed = findLeastUsed();

        contentData.remove(leastUsed.getKey());
        content.remove(leastUsed.getKey());
        log.info("Map capacity limit reached. Removing least used entry: {}", leastUsed.getKey());
    }

    private Map.Entry<K, MetaData> findLeastUsed() {
        Map.Entry<K, MetaData> leastUsed = Collections.min(contentData.entrySet(), (entry1, entry2) ->
        {

            int countComparison = entry1.getValue().getAccessCount().compareTo(entry2.getValue().getAccessCount());
            if (countComparison == 0) {
                return entry1.getValue().getLastAccessed().compareTo(entry2.getValue().getLastAccessed());
            }
            return countComparison;
        });
        return leastUsed;
    }


    protected MetaData getContentDataEntry(K key) {
        return contentData.get(key);
    }

    protected Map<K, V> getContent() {
        return content;
    }

    protected Map<K, MetaData> getContentData() {
        return contentData;
    }

}
