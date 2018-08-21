package com.devinotele.mapper.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldData {
    private String name;
    private String type;
    private List<DtoAnnotation> annotations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DtoAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<DtoAnnotation> annotations) {
        this.annotations = annotations;
    }
}
