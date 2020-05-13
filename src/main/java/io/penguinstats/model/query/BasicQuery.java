package io.penguinstats.model.query;

import java.util.List;

import io.penguinstats.model.DropMatrixElement;

public interface BasicQuery {

	List<DropMatrixElement> execute() throws Exception;

}
