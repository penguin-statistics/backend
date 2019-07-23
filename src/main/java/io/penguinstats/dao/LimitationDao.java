package io.penguinstats.dao;

import io.penguinstats.model.Limitation;

public interface LimitationDao extends BaseDao<Limitation> {

	void removeLimitation(String name);

	void updateLimitation(Limitation limitation);

	Limitation findLimitationByName(String name);

}
