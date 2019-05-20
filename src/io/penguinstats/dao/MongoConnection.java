package io.penguinstats.dao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoConnection {

	private static Logger logger = LogManager.getLogger(MongoConnection.class);
	private static MongoConnection instance = new MongoConnection();

	private MongoClient mongo = null;

	private MongoConnection() {}

	public MongoClient getMongo() throws RuntimeException {
		if (mongo == null) {
			try {
				String path = System.getProperty("user.dir") + File.separator + "webapps" + File.separator
						+ "PenguinStats" + File.separator + "WEB-INF" + File.separator + "mongodb.properties";
				logger.info("Read properties: " + path);
				Properties pps = new Properties();
				InputStream in = new BufferedInputStream(new FileInputStream(path));
				pps.load(in);
				String userName = pps.getProperty("username");
				String password = pps.getProperty("password");
				String hostname = pps.getProperty("hostname");
				Integer port = Integer.valueOf(pps.getProperty("port"));
				in.close();

				MongoCredential credential =
						MongoCredential.createCredential(userName, "penguin_stats", password.toCharArray());
				MongoClientSettings settings =
						MongoClientSettings.builder().credential(credential)
								.applyToSslSettings(builder -> builder.enabled(false))
								.applyToClusterSettings(
										builder -> builder.hosts(Arrays.asList(new ServerAddress(hostname, port))))
								.build();
				mongo = MongoClients.create(settings);
				logger.info("Connection with MongoDB was built.");
			} catch (Exception e) {
				logger.error("Error in getMongo", e);
			}
		}
		return mongo;
	}

	public void init() {
		getMongo();
	}

	public void close() {
		if (mongo != null) {
			try {
				mongo.close();
				mongo = null;
			} catch (Exception e) {
			}
		} else {
		}
	}

	public static MongoConnection getInstance() {
		return instance;
	}

}
