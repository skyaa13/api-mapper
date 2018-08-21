package com.devinotele.mapper.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodParameter {
    public String name;
    public String type;
    public List<AnnotationValue> annotations;

    public List<AnnotationValue> getAnnotations() {
        return annotations;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}