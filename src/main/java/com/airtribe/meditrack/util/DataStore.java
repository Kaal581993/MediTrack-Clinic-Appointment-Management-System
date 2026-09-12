package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataStore<T> {

    private final Map<Integer, T> store = new HashMap<>();
    private final Map<String, T> store2 = new ConcurrentHashMap<>();
    private final List<T> listStore = new ArrayList<>();

    private void upsertList(T entity) {
        listStore.removeIf(existing -> existing == entity || Objects.equals(existing, entity));
        listStore.add(entity);
    }

    private void removeFromList(T entity) {
        listStore.removeIf(existing -> existing == entity || Objects.equals(existing, entity));
    }

    public void add(String key, T value) {
        if (key == null || value == null) {
            return;
        }
        store2.put(key, value);
        try {
            store.put(Integer.parseInt(key), value);
        } catch (NumberFormatException ignored) {
            // Non-numeric keys are supported by the string-backed store.
        }
        upsertList(value);
    }

    public void put(int id, T entity) {
        if (entity == null) {
            return;
        }
        store.put(id, entity);
        store2.put(String.valueOf(id), entity);
        upsertList(entity);
    }

    public void putAll(Collection<? extends T> entities) {
        if (entities == null) {
            return;
        }
        entities.forEach(entity -> {
            if (entity != null) {
                upsertList(entity);
            }
        });
    }

    /**
     * Adds all entities to all internal stores (int-keyed, string-keyed, and list).
     * Use this when entities need to be retrievable by ID via {@link #get(int)}
     * or {@link #containsKey(int)}.
     *
     * @param entities     collection of entities to add
     * @param idExtractor  function that extracts the integer ID from each entity
     */
    public void putAll(Collection<? extends T> entities, java.util.function.Function<T, Integer> idExtractor) {
        if (entities == null) {
            return;
        }
        entities.forEach(entity -> {
            if (entity != null) {
                Integer id = idExtractor.apply(entity);
                if (id == null) {
                    return;
                }
                store.put(id, entity);
                store2.put(String.valueOf(id), entity);
                upsertList(entity);
            }
        });
    }

    public T get(String key) {
        return store2.get(key);
    }

    public Optional<T> get(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean delete(int id) {
        T removed = store.remove(id);
        if (removed == null) {
            return false;
        }
        store2.remove(String.valueOf(id));
        removeFromList(removed);
        return true;
    }

    public boolean containsKey(int id) {
        return store.containsKey(id);
    }

    public void remove(String key) {
        if (key == null) {
            return;
        }
        T value = store2.remove(key);
        if (value == null) {
            return;
        }
        try {
            store.remove(Integer.parseInt(key));
        } catch (NumberFormatException ignored) {
            // Non-numeric keys have no integer-backed entry.
        }
        removeFromList(value);
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
        store.clear();
        store2.clear();
        listStore.clear();
    }
}