package io.penguinstats.model.query;

import java.util.List;

import io.penguinstats.model.DropMatrixElement;

public interface Query {

	List<DropMatrixElement> execute() throws Exception;

}
