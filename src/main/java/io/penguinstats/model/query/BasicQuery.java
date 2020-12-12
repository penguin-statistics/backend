package io.penguinstats.model.query;

import java.util.List;

import io.penguinstats.model.MatrixElement;

public interface BasicQuery {

	List<? extends MatrixElement> execute() throws Exception;

}
