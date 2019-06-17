package io.penguinstats.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import io.penguinstats.dao.MongoConnection;

public class PenguinStatsContextListener implements ServletContextListener {

	MongoConnection conn = MongoConnection.getInstance();

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("PenguinStatsContextListener destroyed");
		conn.close();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("PenguinStatsContextListener started");
	}

}
