package io.penguinstats.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.dao.FrontendConfigDao;
import io.penguinstats.model.FrontendConfig;
import io.penguinstats.util.LastUpdateTimeUtil;

@Service("frontendConfigService")
public class FrontendConfigServiceImpl implements FrontendConfigService {

    @Autowired
    private FrontendConfigDao frontendConfigDao;

    @Override
    public void saveFrontendConfig(FrontendConfig config) {
        frontendConfigDao.save(config);
    }

    @Override
    public void saveFrontendConfig(String key, String value) {
        saveFrontendConfig(new FrontendConfig(null, key, value));
    }

    @Override
    public Map<String, String> getFrontendConfigMap() {
        List<FrontendConfig> configs = frontendConfigDao.findAll();
        LastUpdateTimeUtil.setCurrentTimestamp(LastUpdateMapKeyName.FRONTEND_CONFIG_MAP);
        return configs.stream().collect(Collectors.toMap(FrontendConfig::getKey, FrontendConfig::getValue));
    }

}
