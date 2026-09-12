package com.airtribe.meditrack.interfaces;

public interface Searchable {
    boolean matchesId(int id);
    boolean matchesName(String name);
}