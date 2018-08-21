package com.devinotele.mapper.api.dto;

import com.devinotele.mapper.entity.ControllerAnnotation;
import com.devinotele.mapper.entity.MethodParameter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControllerData {
    private String controllerPath;
    private String methodName;
    private String returnType;
    private List<MethodParameter> parameters;
    private ControllerAnnotation annotation;

    public String getControllerPath() {
        return controllerPath;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }

    public ControllerAnnotation getAnnotation() {
        return annotation;
    }
}
