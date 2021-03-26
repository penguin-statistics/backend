package io.penguinstats.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.SystemPropertyDao;
import io.penguinstats.model.SystemProperty;
import io.penguinstats.util.exception.ServiceException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("systemPropertyService")
public class SystemPropertyServiceImpl implements SystemPropertyService {

    @Autowired
    private SystemPropertyDao systemPropertyDao;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void saveProperty(SystemProperty property) {
        systemPropertyDao.save(property);
    }

    @Override
    public void saveProperty(String key, String value) {
        saveProperty(new SystemProperty(null, key, value));
    }

    @Override
    public SystemProperty getPropertyByKey(String key) {
        return systemPropertyDao.findByKey(key);
    }

    @Override
    public String getPropertyStringValue(String key) {
        return getPropertyStringValue(key, null);
    }

    @Override
    public String getPropertyStringValue(String key, String defaultValue) {
        String str = getSpringProxy().getPropertiesMap().get(key);
        return str == null ? defaultValue : str;
    }

    @Override
    public Integer getPropertyIntegerValue(String key) {
        return getPropertyIntegerValue(key, null);
    }

    @Override
    public Integer getPropertyIntegerValue(String key, Integer defaultValue) {
        String str = getPropertyStringValue(key, null);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.error(str + " cannot be parsed into Integer");
            throw new ServiceException(e);
        }
    }

    @Override
    public Long getPropertyLongValue(String key) {
        return getPropertyLongValue(key, null);
    }

    @Override
    public Long getPropertyLongValue(String key, Long defaultValue) {
        String str = getPropertyStringValue(key, null);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.error(str + " cannot be parsed into Long");
            throw new ServiceException(e);
        }
    }

    @Override
    public Map<String, String> getPropertiesMap() {
        List<SystemProperty> properties = systemPropertyDao.findAll();
        return properties.stream().collect(Collectors.toMap(SystemProperty::getKey, SystemProperty::getValue));
    }

    /** 
     * @Title: getSpringProxy 
     * @Description: Use proxy to hit cache 
     * @return SystemPropertyService
     */
    private SystemPropertyService getSpringProxy() {
        return applicationContext.getBean(SystemPropertyService.class);
    }

}
