package io.penguinstats.service;

import java.util.Collection;
import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import io.penguinstats.enums.Server;
import io.penguinstats.model.PatternMatrixElement;

public interface PatternMatrixElementService {

	void batchSave(Collection<PatternMatrixElement> elements);

	void batchDelete(Server server);

	@Cacheable(value = "pattern-matrix-v2", key = "'pattern-matrix-v2_' + #server", sync = true)
	List<PatternMatrixElement> getGlobalPatternMatrixElements(Server server);

	@CachePut(value = "pattern-matrix-v2", key = "'pattern-matrix-v2_' + #server", condition = "#userID == null")
	List<PatternMatrixElement> generateGlobalPatternMatrixElements(Server server, String userID);

}
