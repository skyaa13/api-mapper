package com.devinotele.mapper.entity;

import com.devinotele.mapper.api.dto.Controllers;
import com.devinotele.mapper.api.dto.DtoData;
import com.devinotele.mapper.api.dto.EnumData;

import java.util.List;
import java.util.Map;

public class ApiData {
    public final Map<String, List<Controllers>> controllers;
    public final List<DtoData> dtoData;
    public final List<EnumData> enumData;

    public ApiData(Map<String, List<Controllers>> controllers, List<DtoData> dtoData, List<EnumData> enumData) {
        this.controllers = controllers;
        this.dtoData = dtoData;
        this.enumData = enumData;
    }
}
