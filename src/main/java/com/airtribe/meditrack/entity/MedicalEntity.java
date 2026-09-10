package com.airtribe.meditrack.entity;

public abstract class MedicalEntity {
    private String entityId;
    private String name;

    public MedicalEntity() {
    }

    public MedicalEntity(String entityId, String name) {
        this.entityId = entityId;
        this.name = name;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
