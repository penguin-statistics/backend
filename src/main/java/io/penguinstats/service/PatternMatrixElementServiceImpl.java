package io.penguinstats.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.ItemDropDao;
import io.penguinstats.dao.PatternMatrixElementDao;
import io.penguinstats.enums.Server;
import io.penguinstats.model.Drop;
import io.penguinstats.model.DropPattern;
import io.penguinstats.model.PatternMatrixElement;
import io.penguinstats.model.QueryConditions;
import io.penguinstats.model.Stage;
import io.penguinstats.model.TimeRange;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("patternMatrixElementServiceImpl")
public class PatternMatrixElementServiceImpl implements PatternMatrixElementService {

	@Autowired
	private PatternMatrixElementDao patternMatrixElementDao;

	@Autowired
	private ItemDropDao itemDropDao;

	@Autowired
	private TimeRangeService timeRangeService;

	@Autowired
	private StageService stageService;

	@Override
	public void batchSave(Collection<PatternMatrixElement> elements) {
		patternMatrixElementDao.saveAll(elements);
	}

	@Override
	public void batchDelete(Server server) {
		patternMatrixElementDao.deleteByServer(server);
	}

	@Override
	public List<PatternMatrixElement> getGlobalPatternMatrixElements(Server server) {
		return patternMatrixElementDao.findByServer(server);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PatternMatrixElement> generateGlobalPatternMatrixElements(Server server, String userID) {
		Long startTime = System.currentTimeMillis();

		Map<String, TimeRange> timeRangesMap = timeRangeService.getLatestTimeRangesMapByServer(server);
		Map<String, Stage> stageMap = stageService.getStageMap();
		QueryConditions conditions = new QueryConditions();
		if (server != null)
			conditions.addServer(server);
		if (userID != null)
			conditions.addUserID(userID);
		timeRangesMap.forEach((stageId, range) -> {
			if (stageMap.containsKey(stageId) && !Boolean.TRUE.equals(stageMap.get(stageId).getIsGacha()))
				conditions.addStage(stageId, range.getStart(), range.getEnd());
		});

		List<PatternMatrixElement> result = new ArrayList<>();
		List<Document> docs = itemDropDao.aggregateDropPatterns(conditions);
		docs.forEach(doc -> {
			String stageId = doc.getString("stageId");
			Integer quantity = doc.getInteger("quantity");
			Integer times = doc.getInteger("times");
			List<Document> dropsDocs = (List<Document>)doc.get("pattern");

			List<Drop> drops = new ArrayList<>();
			dropsDocs.forEach(dropDoc -> {
				String itemId = dropDoc.getString("itemId");
				Integer innerQuantity = dropDoc.getInteger("quantity");
				Drop drop = new Drop(itemId, innerQuantity);
				drops.add(drop);
			});

			DropPattern pattern = new DropPattern(drops);
			TimeRange range = timeRangesMap.get(stageId);
			PatternMatrixElement element = new PatternMatrixElement(null, stageId, pattern, quantity, times,
					range.getStart(), range.getEnd(), server, System.currentTimeMillis());
			result.add(element);
		});

		if (userID == null) {
			log.info("generateGlobalPatternMatrixElements done in {} ms for server {}",
					System.currentTimeMillis() - startTime, server);
		}

		return result;
	}

}
