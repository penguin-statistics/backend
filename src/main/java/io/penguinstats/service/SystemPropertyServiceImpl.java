package io.penguinstats.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.SystemPropertyDao;
import io.penguinstats.model.SystemProperty;

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
		return getSpringProxy().getPropertiesMap().get(key);
	}

	@Override
	public Integer getPropertyIntegerValue(String key) {
		try {
			return Integer.parseInt(getPropertyStringValue(key));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public Long getPropertyLongValue(String key) {
		try {
			return Long.parseLong(getPropertyStringValue(key));
		} catch (NumberFormatException e) {
			return null;
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
