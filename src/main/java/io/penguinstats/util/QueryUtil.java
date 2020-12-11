package io.penguinstats.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.penguinstats.model.MatrixElement;

public class QueryUtil {

	public static List<? extends MatrixElement> runQuery(Callable<List<? extends MatrixElement>> func, Integer timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		Future<List<? extends MatrixElement>> future = singleThreadExecutor.submit(func);
		return timeout != null ? future.get(timeout, TimeUnit.MINUTES) : future.get();
	}

}
