package io.penguinstats.service;

import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.OutlierDao;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.model.Outlier;
import io.penguinstats.util.exception.BusinessException;

@Service("outlierServiceImpl")
public class OutlierServiceImpl implements OutlierService {

    @Autowired
    private OutlierDao outlierDao;

    @Override
    public void saveOutlier(Outlier outlier) {
        outlierDao.save(outlier);
    }

    @Override
    public Outlier saveOutlier(Document metadata, String bucket, String userID) {
        Outlier outlier = new Outlier(metadata, bucket, userID);
        saveOutlier(outlier);
        return outlier;
    }

    @Override
    public Outlier getOutlierById(String id) {
        return outlierDao.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND, "Outlier[" + id + "] is not found", Optional.of(id)));
    }

}
