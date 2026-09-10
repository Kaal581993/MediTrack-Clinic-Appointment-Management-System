package com.airtribe.meditrack.inter_face;

import java.util.List;

public interface Searchable<T> {

    List<T> searchById(int id);

    List<T> searchAll();

    default boolean exists(int id) {
        return !searchById(id).isEmpty();
    }

    default int count() {
        return searchAll().size();
    }
}
