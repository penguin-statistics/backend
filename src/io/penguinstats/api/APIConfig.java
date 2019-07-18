package io.penguinstats.api;

import org.glassfish.jersey.server.ResourceConfig;

import io.penguinstats.api.filter.CORSFilter;
import io.penguinstats.api.filter.ReadUserIDCookieFilter;
import io.penguinstats.api.filter.ReadUserIDCookieOrCreateFilter;
import io.penguinstats.api.filter.SetNoStoreHeaderFilter;
import io.penguinstats.api.filter.SetUserIDCookieFilter;

public class APIConfig extends ResourceConfig {

	public APIConfig() {
		this.register(ReadUserIDCookieFilter.class).register(ReadUserIDCookieOrCreateFilter.class)
				.register(SetUserIDCookieFilter.class);
		this.register(CORSFilter.class);
		this.register(SetNoStoreHeaderFilter.class);
		this.packages("io.penguinstats.api");
		System.out.println("APIConfig finished");
	}

}
