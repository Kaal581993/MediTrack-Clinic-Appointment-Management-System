package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataStore<T> {

    private final Map<String, T> store = new ConcurrentHashMap<>();
    private final List<T> listStore = new ArrayList<>();

    public void add(String key, T value) {
        store.put(key, value);
        listStore.add(value);
    }

    public T get(String key) {
        return store.get(key);
    }

    public void remove(String key) {
        T value = store.remove(key);
        if (value != null) {
            listStore.remove(value);
        }
    }

    public List<T> getAll() {
        return new ArrayList<>(listStore);
    }

    public List<T> filter(Predicate<T> predicate) {
        return listStore.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public boolean contains(String key) {
        return store.containsKey(key);
    }

    public int size() {
        return listStore.size();
    }

    public void clear() {
        store.clear();
        listStore.clear();
    }
}
