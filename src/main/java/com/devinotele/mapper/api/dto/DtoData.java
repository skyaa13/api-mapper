package com.devinotele.mapper.api.dto;

import com.devinotele.mapper.entity.FieldData;

import java.util.List;

public class DtoData {
    private String className;
    private List<FieldData> fields;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<FieldData> getFields() {
        return fields;
    }

    public void setFields(List<FieldData> fields) {
        this.fields = fields;
    }
}
