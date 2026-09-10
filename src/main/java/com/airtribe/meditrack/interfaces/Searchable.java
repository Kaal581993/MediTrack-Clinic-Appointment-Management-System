package com.airtribe.meditrack.interfaces;

public interface Searchable {
    boolean matches(String searchTerm);
    String getSearchKey();
    boolean matchesId(int id);
    boolean matchesName(String name);
}
