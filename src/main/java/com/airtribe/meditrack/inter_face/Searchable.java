package com.airtribe.meditrack.inter_face;

import java.util.List;

/**
 * Contract for any service that exposes lookup over the records it manages.
 * <p>
 * Generic in {@code T} so doctor, patient and appointment services can all implement
 * it against their own record type without casting.
 *
 * @param <T> the type of record being searched
 */
public interface Searchable<T> {

    /**
     * Finds records matching a generated id.
     *
     * @param id the id to look for
     * @return matching records, or an empty list when nothing matches; never {@code null}
     */
    List<T> searchById(int id);

    /**
     * @return every record currently held; never {@code null}
     */
    List<T> searchAll();

    /**
     * Convenience check built on {@link #searchById(int)}, provided as a default method
     * so implementations get it for free.
     *
     * @param id the id to look for
     * @return {@code true} when at least one record carries that id
     */
    default boolean exists(int id) {
        return !searchById(id).isEmpty();
    }

    /**
     * @return how many records are currently held
     */
    default int count() {
        return searchAll().size();
    }
}
