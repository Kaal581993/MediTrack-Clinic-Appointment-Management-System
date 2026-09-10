package com.airtribe.meditrack.inter_face;

public interface Searchable {
    boolean matches(String searchTerm);
    String getSearchKey();
}
