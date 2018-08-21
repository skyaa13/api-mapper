package com.devinotele.mapper.service;

import com.devinotele.mapper.api.dto.Controllers;
import com.devinotele.mapper.api.dto.DtoData;
import com.devinotele.mapper.api.dto.EnumData;
import com.devinotele.mapper.entity.AnnotationValue;
import com.devinotele.mapper.entity.ApiData;
import com.devinotele.mapper.entity.MethodParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Service
public class Mapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mapper.class);

    private final String numKey = "numeric";
    private final String strKey = "strings";
    private final String mailKey = "emails";
    private final String phoneKey = "phones";

    private RestTemplate restTemplate;
    private ApiReceiver apiReceiver;
    private ApiData apiData;
    private String baseUrl;
    private Map<String, List<Object>> validationMap = new HashMap<>();

    public Mapper(ApiReceiver apiReceiver,
                  RestTemplate restTemplate,
                  @Value("${base.url}") String baseUrl) {
        this.apiReceiver = apiReceiver;
        this.apiData = apiReceiver.getApiData();
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    {
        validationMap.put(numKey, Arrays.asList(-1, 0, 1, Integer.MAX_VALUE, Long.MAX_VALUE));
        validationMap.put(strKey, Arrays.asList("abc", ""));
        validationMap.put(mailKey, Arrays.asList("mail@example", "", "@mail.ru"));
        validationMap.put(phoneKey, Arrays.asList("89160001122", "8(916)0001122", "8-916-000-11-22", "8 916 000 11 22"));
    }

    public void work() {
        validationMap.forEach((key, value) -> value.forEach(object -> postRequests(key, object)));
        getRequests();
    }

    private void postRequests(String key, Object value) {
        System.out.println(key);
        Map<String, List<Controllers>> controllers = apiData.controllers;
        List<EnumData> enums = apiData.enumData;
        List<DtoData> dtoData = apiData.dtoData;

        assert controllers != null;

        controllers.get("post").forEach(c -> c.getControllerData().forEach(cd -> {
            UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);
            String controllerPath = cd.getControllerPath();
            String annotationPath = cd.getAnnotation().getAnnotationPath();

            if (cd.getAnnotation().getAnnotationName().equals(PostMapping.class.getSimpleName())) {
                List<MethodParameter> params = cd.getParameters();
                String path = "";
                String replacement = "";
                Map<String, Object> requestObject = new HashMap<>();
                if (params.size() > 0) {
                    for (MethodParameter param : params) {
                        for (AnnotationValue annotation : param.getAnnotations()) {
                            if (annotation.getName().contains(PathVariable.class.getSimpleName())) {
                                if (param.getType().contains(int.class.getSimpleName())
                                        || param.getType().contains(Integer.class.getSimpleName())
                                        || param.getType().contains(long.class.getSimpleName())
                                        || param.getType().contains(Long.class.getSimpleName())) {
                                    replacement = isNumeric(value) ? String.valueOf(value) : "1";
                                }
                                String removeAllBetweenBracers = "\\{.*?\\} ?";
                                path = annotationPath.replaceAll(removeAllBetweenBracers, replacement);
                            }

                            if (annotation.getName().contains(RequestBody.class.getSimpleName())) {
                                DtoData requestBodyDto = getRequestBodyDto(param.getType(), dtoData);

                                requestBodyDto.getFields().forEach(fieldData -> {
                                    Object fieldValue = null;
                                    if (fieldData.getType().contains(Integer.class.getSimpleName())
                                            || fieldData.getType().contains(int.class.getSimpleName())
                                            || fieldData.getType().contains(Long.class.getSimpleName())
                                            || fieldData.getType().contains(long.class.getSimpleName())
                                            || fieldData.getType().contains(Byte.class.getSimpleName())
                                            || fieldData.getType().contains(byte.class.getSimpleName())
                                            || fieldData.getType().contains(BigDecimal.class.getSimpleName())) {
                                        fieldValue = isNumeric(value) ? String.valueOf(value) : "1";
                                    }

                                    if (fieldData.getType().contains(String.class.getSimpleName())) {
                                        if (fieldData.getName().contains("phone")) {
                                            if (key.equals(phoneKey)) {
                                                fieldValue = String.valueOf(value);
                                            } else {
                                                fieldValue = "8916" + randomNumeric(7);
                                            }
                                        } else if (fieldData.getName().contains("email")) {
                                            if (key.equals(mailKey)) {
                                                fieldValue = String.valueOf(value);
                                            } else {
                                                fieldValue = "text@example.com";
                                            }
                                        } else {
                                            fieldValue = randomAlphabetic(5).toLowerCase();
                                        }
                                    }

                                    if (fieldData.getType().contains("Date")) {
                                        if (fieldData.getName().contains("start") || fieldData.getName().contains("from") ||
                                                fieldData.getName().contains("begin")) {
                                            fieldValue = ZonedDateTime.now().toLocalDate().toString();
                                        } else {
                                            fieldValue = ZonedDateTime.now().toLocalDate().plusDays(1).toString();
                                        }
                                    }

                                    if (fieldData.getType().contains(boolean.class.getSimpleName())
                                            || fieldData.getType().contains(Boolean.class.getSimpleName())) {
                                        fieldValue = true;
                                    }

                                    if (isEnumType(fieldData.getType(), enums)) {
                                        EnumData enumData = getEnumType(fieldData.getType(), enums);
                                        fieldValue = enumData.getValues().get(0);
                                    }

                                    requestObject.put(fieldData.getName(), fieldValue);
                                });
                            }
                        }
                    }
                }

                componentsBuilder.path(controllerPath + path);

                if (key.equals(mailKey) && !requestObject.keySet().contains("email")) {
                    return;
                }

                if (key.equals(phoneKey) && !requestObject.keySet().contains("phone")) {
                    return;
                }

                LOGGER.info("Url: {}", componentsBuilder.toUriString());
                LOGGER.info("Request: {}", mapToString(requestObject));
                String response = restTemplate.postForObject(componentsBuilder.toUriString(), requestObject, String.class);
                LOGGER.info("Response: " + response);
                requestObject.clear();
            }
        }));
    }


    private void getRequests() {
        Map<String, List<Controllers>> controllers = apiData.controllers;
        List<EnumData> enums = apiData.enumData;

        assert enums != null;
        assert controllers != null;

        controllers.get("get").forEach(c -> c.getControllerData().forEach(cd -> {
            UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);
            String controllerPath = cd.getControllerPath();
            String annotationPath = cd.getAnnotation().getAnnotationPath();
            if (cd.getAnnotation().getAnnotationName().equals(GetMapping.class.getSimpleName())) {
                List<MethodParameter> params = cd.getParameters();
                String path = "";
                String replacement = "";
                if (params.size() > 0) {
                    for (MethodParameter param : params) {
                        String paramName = "";
                        String paramValue = "1";
                        for (AnnotationValue annotation : param.getAnnotations()) {
                            if (annotation.getName().contains(PathVariable.class.getSimpleName())) {
                                if (param.getType().contains("int") || param.getType().contains("Integer") ||
                                        param.getType().contains("long") || param.getType().contains("Long")) {
                                    replacement = "1";
                                }
                                path = annotationPath.replaceAll("\\{.*?\\} ?", replacement);
                            }
                            if (annotation.getName().contains(RequestParam.class.getSimpleName())) {
                                paramName = param.getName();
                            }
                            if (annotation.getName().contains(Min.class.getSimpleName())) {
                                paramValue = annotation.getValue();
                            }
                        }
                        if (param.getAnnotations().stream().anyMatch(a -> a.getName().contains(RequestParam.class.getSimpleName()))) {
                            if (isEnumType(param.getType(), enums)) {
                                EnumData enumData = getEnumType(param.getType(), enums);
                                componentsBuilder.queryParam(param.getName(), enumData.getValues().get(0));
                            } else if (param.getType().contains("Date")) {
                                componentsBuilder.queryParam(paramName, ZonedDateTime.now().toLocalDate());
                            } else {
                                componentsBuilder.queryParam(paramName, paramValue);
                            }
                        }
                    }
                }
                componentsBuilder.path(controllerPath + path);
                LOGGER.info("Url: {}", componentsBuilder.toUriString());

                String response = restTemplate.getForObject(componentsBuilder.toUriString(), String.class);
                LOGGER.info("Response: " + response);
            }
        }));
    }

    private DtoData getRequestBodyDto(String paramType, List<DtoData> dtoData) {
        return dtoData.stream()
                .filter(dto -> cutFromLastDot(dto.getClassName()).equals(cutFromLastDot(paramType)))
                .findFirst()
                .get();
    }

    private String mapToString(Object requestObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isNumeric(Object object) {
        return object instanceof Number;
    }

    private EnumData getEnumType(String type, List<EnumData> enums) {
        return enums.stream().filter(e -> e.getEnumName().contains(cutFromLastDot(type))).findAny().get();
    }

    private boolean isEnumType(String type, List<EnumData> enums) {
        return enums.stream().anyMatch(e -> e.getEnumName().contains(cutFromLastDot(type)));
    }

    private String cutFromLastDot(String str) {
        return str.substring(str.lastIndexOf(".") + 1);
    }
}