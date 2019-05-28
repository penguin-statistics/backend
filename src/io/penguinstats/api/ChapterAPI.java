package io.penguinstats.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import io.penguinstats.bean.Chapter;
import io.penguinstats.bean.Stage;
import io.penguinstats.service.ChapterService;
import io.penguinstats.service.StageService;

@Path("/chapter")
public class ChapterAPI {

	private static final ChapterService chapterService = ChapterService.getInstance();
	private static final StageService stageService = StageService.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChapters() {
		List<Chapter> chapters = chapterService.getAllChapters();
		return Response.ok(new JSONObject().put("chapters", chapters).toString()).build();
	}

	@GET
	@Path("/{chapterID}/stage")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStages(@PathParam("chapterID") Integer chapterID) {
		if (chapterID == null)
			return Response.status(Status.BAD_REQUEST).build();
		Chapter chapter = chapterService.getChapter(chapterID);
		if (chapter == null)
			return Response.status(404).build();
		Map<Integer, Stage> stageMap = stageService.getStageMap();
		List<Stage> stages = new ArrayList<>();
		for (Integer stageID : chapter.getStages())
			stages.add(stageMap.get(stageID));
		return Response.ok(new JSONObject().put("stages", stages).toString()).build();
	}

}
