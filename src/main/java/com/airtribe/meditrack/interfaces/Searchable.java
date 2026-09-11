package com.airtribe.meditrack.interfaces;

public interface Searchable {
    boolean matches(String searchTerm);

    String getSearchKey();

    boolean matchesId(int id);

    boolean matchesName(String name);

    /**
     * Returns whether this searchable entity matches the supplied identifier.
     *
     * @param id identifier to match
     * @return {@code true} when the identifier matches
     */
    default boolean exists(int id) {
        return matchesId(id);
    }

    /**
     * Returns the number of records represented by this searchable object.
     * Entity implementations represent one record; collection-backed services
     * override this method with their actual count.
     *
     * @return record count
     */
    default int count() {
        return 1;
    }
}
