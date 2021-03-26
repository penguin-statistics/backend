package io.penguinstats.service;

import org.bson.Document;

import io.penguinstats.model.Outlier;

public interface OutlierService {

    void saveOutlier(Outlier outlier);

    Outlier saveOutlier(Document metadata, String bucket, String userID);

    Outlier getOutlierById(String id);

}
