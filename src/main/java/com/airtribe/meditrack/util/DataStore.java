package com.airtribe.meditrack.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataStore<T> {

    private final Map<Integer, T> store = new HashMap<>();
    private final Map<String, T> store2 = new ConcurrentHashMap<>();
    private final List<T> listStore = new ArrayList<>();

    public void add(String key, T value) {
        store2.put(key, value);
        listStore.add(value);
    }

    public void put(int id, T entity) {
        store.put(id, entity);
    }


    public T get(String key) {
        return store2.get(key);
    }


    public Optional<T> get(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean delete(int id) {
        return store.remove(id) != null;
    }

    public boolean containsKey(int id) {
        return store.containsKey(id);
    }

    public void remove(String key) {
        T value = store2.remove(key);
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
        return store2.containsKey(key);
    }

    public int size() {
        return listStore.size();
    }

    public void clear() {
        store2.clear();
        listStore.clear();
    }
}