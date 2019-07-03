package io.penguinstats.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/formula")
public class FormulaAPI {

	private static Logger logger = LogManager.getLogger(FormulaAPI.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormula() {
		logger.info("GET /formula");
		try {
			String path = null;
			if (System.getProperty("user.dir").contains("PenguinStats")) {
				path = System.getProperty("user.dir") + File.separator + "WebContent" + File.separator + "WEB-INF"
						+ File.separator + "json" + File.separator + "formula.json";
			} else {
				path = System.getProperty("user.dir") + File.separator + "webapps" + File.separator + "PenguinStats"
						+ File.separator + "WEB-INF" + File.separator + "json" + File.separator + "formula.json";
			}
			BufferedReader reader = new BufferedReader(new FileReader(path));
			StringBuilder builder = new StringBuilder();
			String currentLine = reader.readLine();
			while (currentLine != null) {
				builder.append(currentLine).append("\n");
				currentLine = reader.readLine();
			}
			reader.close();
			return Response.ok(builder.toString()).build();
		} catch (Exception e) {
			logger.error("Error in getFormula", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
