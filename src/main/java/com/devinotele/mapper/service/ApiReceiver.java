package com.devinotele.mapper.service;

import com.devinotele.mapper.api.dto.Controllers;
import com.devinotele.mapper.api.dto.DtoData;
import com.devinotele.mapper.api.dto.EnumData;
import com.devinotele.mapper.entity.ApiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ApiReceiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiReceiver.class);

    private final String baseUrl;
    private RestTemplate restTemplate;

    public ApiReceiver(@Value("${base.url}") String baseUrl,
                       RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    public ApiData getApiData() {
        Map<String, List<Controllers>> controllersMap = new HashMap<>();

        controllersMap.put("post", getControllerData("post"));
        controllersMap.put("get", getControllerData("get"));
        controllersMap.put("put", getControllerData("put"));
        controllersMap.put("patch", getControllerData("patch"));

        return new ApiData(controllersMap, getDtoData(), getEnums());
    }

    private List<Controllers> getControllerData(String type) {
        Controllers[] controllers;
        try {
            controllers = restTemplate.getForObject(baseUrl + "/api-data/ctrl/" + type, Controllers[].class);
        } catch (Throwable t) {
            LOGGER.info("Controllers {} was not found", type);
            controllers = new Controllers[0];
        }
        return Arrays.asList(Objects.requireNonNull(controllers));
    }

    private List<DtoData> getDtoData() {
        DtoData[] dtoData;
        try {
            dtoData = restTemplate.getForObject(baseUrl + "/api-data/dto", DtoData[].class);
        } catch (Throwable t) {
            LOGGER.info("Enums was not found, error occurred {}", t);
            dtoData = new DtoData[0];
        }
        return Arrays.asList(Objects.requireNonNull(dtoData));
    }

    private List<EnumData> getEnums() {
        EnumData[] enumData;
        try {
            enumData = restTemplate.getForObject(baseUrl + "/api-data/enums", EnumData[].class);
        } catch (Throwable t) {
            LOGGER.info("Enums was not found, error occurred {}", t);
            enumData = new EnumData[0];
        }
        return Arrays.asList(Objects.requireNonNull(enumData));
    }
}
