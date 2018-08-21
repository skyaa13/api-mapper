package com.devinotele.mapper.api.dto;

import java.util.List;
import java.util.Map;

public class Controllers {
    public String declaredClass;
    public List<ControllerData> controllerData;

    public String getDeclaredClass() {
        return declaredClass;
    }

    public List<ControllerData> getControllerData() {
        return controllerData;
    }
}
