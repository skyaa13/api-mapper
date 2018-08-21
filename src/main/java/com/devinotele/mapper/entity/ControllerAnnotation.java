package com.devinotele.mapper.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControllerAnnotation {
    private String annotationName;
    private String annotationPath;

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public String getAnnotationPath() {
        return annotationPath;
    }

    public void setAnnotationPath(String annotationPath) {
        this.annotationPath = annotationPath;
    }
}
