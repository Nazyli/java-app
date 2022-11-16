package com.nazyli.javaapp;

import com.nazyli.javaapp.dto.PropertiesDto;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.*;

@Component
public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {
    private static final Logger log = LoggerFactory.getLogger(PropertiesLogger.class);

    private ConfigurableEnvironment environment;
    private boolean isFirstRun = true;

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if (isFirstRun) {
            environment = event.getApplicationContext().getEnvironment();
//            printProperties();
        }
        isFirstRun = false;
    }

    public List<PropertiesDto> printProperties() {
        PropertiesDto dto;
        List<PropertiesDto> listDto = new ArrayList<>();
        Map<String,String> propertyNameMap;


        for (EnumerablePropertySource propertySource : findPropertiesPropertySources()) {
            dto = new PropertiesDto();
            propertyNameMap = new HashMap<>();
            dto.setName(propertySource.getName());
//            log.info("******* " + propertySource.getName() + " *******");

            String[] propertyNames = propertySource.getPropertyNames();
            Arrays.sort(propertyNames);
            for (String propertyName : propertyNames) {
                String resolvedProperty = environment.getProperty(propertyName);
                String sourceProperty = Objects.requireNonNull(propertySource.getProperty(propertyName)).toString();
                assert resolvedProperty != null;
                if(resolvedProperty.equals(sourceProperty)) {
                    propertyNameMap.put(propertyName, resolvedProperty);
//                    log.info("{}={}", propertyName, resolvedProperty);
                }else {
                    propertyNameMap.put(propertyName, resolvedProperty +" OVERRIDDEN to " + resolvedProperty);
//                    log.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
                }
            }
            dto.setData(propertyNameMap);
            listDto.add(dto);
        }
        return listDto;
    }

    private List<EnumerablePropertySource> findPropertiesPropertySources() {
        List<EnumerablePropertySource> propertiesPropertySources = new LinkedList<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                propertiesPropertySources.add((EnumerablePropertySource) propertySource);
            }
        }
        return propertiesPropertySources;
    }
}