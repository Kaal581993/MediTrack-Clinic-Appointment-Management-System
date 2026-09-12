package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataStore<T> {
    private final Map<Integer, T> store = new HashMap<>();

    public void put(int id, T entity) {
        store.put(id, entity);
    }

    public Optional<T> get(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> getAll() {
        return new ArrayList<>(store.values());
    }

    public boolean delete(int id) {
        return store.remove(id) != null;
    }

    public boolean containsKey(int id) {
        return store.containsKey(id);
    }
}