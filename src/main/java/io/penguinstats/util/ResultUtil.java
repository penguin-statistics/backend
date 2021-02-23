package io.penguinstats.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.controller.v2.mapper.QueryMapper;
import io.penguinstats.controller.v2.request.AdvancedQueryRequest;
import io.penguinstats.controller.v2.response.AdvancedQueryResponse;
import io.penguinstats.controller.v2.response.BasicQueryResponse;
import io.penguinstats.controller.v2.response.MatrixQueryResponse;
import io.penguinstats.controller.v2.response.PatternQueryResponse;
import io.penguinstats.controller.v2.response.TrendQueryResponse;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.enums.QueryType;
import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.PatternMatrixElement;
import io.penguinstats.model.query.BasicQuery;
import io.penguinstats.model.query.GlobalMatrixQuery;
import io.penguinstats.model.query.GlobalPatternQuery;
import io.penguinstats.model.query.QueryFactory;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.DropMatrixElementService;
import io.penguinstats.service.PatternMatrixElementService;
import io.penguinstats.service.SystemPropertyService;
import io.penguinstats.util.exception.ServiceException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component("resultUtil")
public class ResultUtil {

    private static ResultUtil resultUtil;

    @Autowired
    private DropInfoService dropInfoService;
    @Autowired
    private DropMatrixElementService dropMatrixElementService;
    @Autowired
    private PatternMatrixElementService patternMatrixElementService;
    @Autowired
    private SystemPropertyService systemPropertyService;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private QueryMapper queryMapper;
    @Autowired
    private QueryFactory queryFactory;

    @PostConstruct
    public void init() {
        resultUtil = this;
        resultUtil.dropInfoService = this.dropInfoService;
        resultUtil.dropMatrixElementService = this.dropMatrixElementService;
        resultUtil.patternMatrixElementService = this.patternMatrixElementService;
        resultUtil.systemPropertyService = this.systemPropertyService;
        resultUtil.cookieUtil = this.cookieUtil;
        resultUtil.queryFactory = this.queryFactory;
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<MatrixQueryResponse> getMatrixHelper(HttpServletRequest request, Server server,
            boolean showClosedZones, String stageFilter, String itemFilter, boolean isPersonal) throws Exception {
        log.info("GET /matrix");

        String userID = isPersonal ? cookieUtil.readUserIDFromCookie(request) : null;
        if (isPersonal && userID == null) {
            return new ResponseEntity<>(new MatrixQueryResponse(new ArrayList<>()), HttpStatus.OK);
        }

        List<DropMatrixElement> pastElements = null;
        List<DropMatrixElement> currentElements = null;
        if (userID != null) {
            GlobalMatrixQuery pastQuery = (GlobalMatrixQuery)queryFactory.getQuery(QueryType.GLOBAL_MATRIX);
            Integer pastTimeout =
                    systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.PAST_GLOBAL_MATRIX_QUERY_TIMEOUT);
            pastQuery.setServer(server).setUserID(userID).setIsPast(true);
            if (pastTimeout != null)
                pastQuery.setTimeout(pastTimeout);
            pastElements = (List<DropMatrixElement>)pastQuery.execute();

            GlobalMatrixQuery currentQuery = (GlobalMatrixQuery)queryFactory.getQuery(QueryType.GLOBAL_MATRIX);
            Integer currentTimeout = systemPropertyService
                    .getPropertyIntegerValue(SystemPropertyKey.CURRENT_GLOBAL_MATRIX_QUERY_TIMEOUT);
            currentQuery.setServer(server).setUserID(userID).setIsPast(false);
            if (currentTimeout != null)
                currentQuery.setTimeout(currentTimeout);
            currentElements = (List<DropMatrixElement>)currentQuery.execute();
        } else {
            pastElements = dropMatrixElementService.getGlobalDropMatrixElements(server, true);
            if (pastElements.isEmpty()) {
                Thread.sleep(1000L);
                pastElements = dropMatrixElementService.getGlobalDropMatrixElements(server, true);
                if (pastElements.isEmpty()) {
                    log.error("past global drop matrix elements shouldn't be empty");
                }
            }

            currentElements = dropMatrixElementService.getGlobalDropMatrixElements(server, false);
            if (currentElements.isEmpty()) {
                Thread.sleep(1000L);
                currentElements = dropMatrixElementService.getGlobalDropMatrixElements(server, false);
                if (currentElements.isEmpty()) {
                    log.error("current global drop matrix elements shouldn't be empty");
                }
            }
        }
        List<DropMatrixElement> elements = DropMatrixElementUtil.combineElementLists(pastElements, currentElements);

        if (!showClosedZones)
            removeClosedStages(elements, server);

        if (stageFilter != null)
            filterStages(elements, stageFilter);

        if (itemFilter != null)
            filterItems(elements, itemFilter);

        HttpHeaders headers = new HttpHeaders();
        if (userID == null && !elements.isEmpty()) {
            DropMatrixElement maxLastUpdateTimeElement =
                    elements.stream().max(Comparator.comparing(DropMatrixElement::getUpdateTime))
                            .orElseThrow(NoSuchElementException::new);
            Long lastUpdateTime = maxLastUpdateTimeElement.getUpdateTime();
            String lastModified = DateUtil.formatDate(new Date(lastUpdateTime));
            headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
        }

        elements.forEach(DropMatrixElement::toResultView);
        MatrixQueryResponse result = new MatrixQueryResponse(elements);

        return new ResponseEntity<MatrixQueryResponse>(result, headers, HttpStatus.OK);
    }

    public ResponseEntity<TrendQueryResponse> getTrendHelper(Server server) throws Exception {
        List<DropMatrixElement> elements = dropMatrixElementService.getGlobalTrendElements(server);
        if (elements.isEmpty()) {
            Thread.sleep(1000L);
            elements = dropMatrixElementService.getGlobalTrendElements(server);
            if (elements.isEmpty()) {
                log.error("global trend shouldn't be empty");
            }
        }

        HttpHeaders headers = new HttpHeaders();
        if (!elements.isEmpty()) {
            DropMatrixElement maxLastUpdateTimeElement =
                    elements.stream().max(Comparator.comparing(DropMatrixElement::getUpdateTime))
                            .orElseThrow(NoSuchElementException::new);
            Long lastUpdateTime = maxLastUpdateTimeElement.getUpdateTime();
            String lastModified = DateUtil.formatDate(new Date(lastUpdateTime));
            headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
        }

        elements.forEach(DropMatrixElement::toResultView);
        TrendQueryResponse result = new TrendQueryResponse(elements);

        return new ResponseEntity<TrendQueryResponse>(result, headers, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<PatternQueryResponse> getPatternHelper(HttpServletRequest request, Server server,
            boolean isPersonal) throws Exception {
        log.info("GET /pattern");

        String userID = isPersonal ? cookieUtil.readUserIDFromCookie(request) : null;
        if (isPersonal && userID == null) {
            return new ResponseEntity<PatternQueryResponse>(new PatternQueryResponse(new ArrayList<>()), HttpStatus.OK);
        }

        List<PatternMatrixElement> elements = null;
        try {
            if (userID != null) {
                GlobalPatternQuery pastQuery = (GlobalPatternQuery)queryFactory.getQuery(QueryType.GLOBAL_PATTERN);
                Integer pastTimeout =
                        systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.GLOBAL_PATTERN_QUERY_TIMEOUT);
                pastQuery.setServer(server).setUserID(userID);
                if (pastTimeout != null)
                    pastQuery.setTimeout(pastTimeout);
                elements = (List<PatternMatrixElement>)pastQuery.execute();
            } else {
                elements = patternMatrixElementService.getGlobalPatternMatrixElements(server);
                if (elements.isEmpty()) {
                    Thread.sleep(1000L);
                    elements = patternMatrixElementService.getGlobalPatternMatrixElements(server);
                    if (elements.isEmpty()) {
                        log.error("global pattern matrix elements shouldn't be empty");
                        throw new ServiceException();
                    }
                }
            }
        } catch (ExecutionException ex) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), ex);
        } catch (TimeoutException ex) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "pastQuery execute time out.", ex);
        } catch (InterruptedException ex) {
            log.error("Thread is interrupted.");
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Thread is interrupted.", ex);
        }

        HttpHeaders headers = new HttpHeaders();
        if (userID == null && !elements.isEmpty()) {
            PatternMatrixElement maxLastUpdateTimeElement = elements.stream()
                    .max(Comparator.comparing(PatternMatrixElement::getUpdateTime)).orElseThrow(ServiceException::new);
            Long lastUpdateTime = maxLastUpdateTimeElement.getUpdateTime();
            String lastModified = DateUtil.formatDate(new Date(lastUpdateTime));
            headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
        }

        elements.forEach(PatternMatrixElement::toResultView);
        PatternQueryResponse result = new PatternQueryResponse(elements);

        return new ResponseEntity<PatternQueryResponse>(result, headers, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<AdvancedQueryResponse> getAdvancedResultHelper(AdvancedQueryRequest advancedQueryRequest,
            HttpServletRequest request) {
        Integer maxQueryNum =
                systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.ADVANCED_QUERY_REQUEST_NUM_MAX);
        if (advancedQueryRequest.getQueries().size() > maxQueryNum) {
            AdvancedQueryResponse advancedQueryResponse =
                    new AdvancedQueryResponse("Too many quiries. Max num is " + maxQueryNum);
            return new ResponseEntity<>(advancedQueryResponse, HttpStatus.BAD_REQUEST);
        }
        final String userIDFromCookie = cookieUtil.readUserIDFromCookie(request);
        List<BasicQueryResponse> results = new ArrayList<>();
        advancedQueryRequest.getQueries().forEach(singleQuery -> {
            try {
                Boolean isPersonal = Optional.ofNullable(singleQuery.getIsPersonal()).orElse(false);
                String userID = isPersonal ? userIDFromCookie : null;
                Integer timeout =
                        systemPropertyService.getPropertyIntegerValue(SystemPropertyKey.ADVANCED_QUERY_TIMEOUT);
                BasicQuery query = queryMapper.queryRequestToQueryModel(singleQuery, userID, timeout);
                List<DropMatrixElement> elements = (List<DropMatrixElement>)query.execute();
                elements.forEach(DropMatrixElement::toResultView);
                BasicQueryResponse queryResponse = queryMapper.elementsToBasicQueryResponse(singleQuery, elements);
                results.add(queryResponse);
            } catch (TimeoutException ex) {
                throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "pastQuery execute time out.", ex);
            } catch (Exception ex) {
                throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
            }
        });
        AdvancedQueryResponse advancedQueryResponse = new AdvancedQueryResponse(results);
        return new ResponseEntity<AdvancedQueryResponse>(advancedQueryResponse, HttpStatus.OK);
    }

    private void removeClosedStages(List<DropMatrixElement> elements, Server server) {
        Set<String> openingStages = dropInfoService.getOpeningStages(server, System.currentTimeMillis());
        Iterator<DropMatrixElement> iter = elements.iterator();
        while (iter.hasNext()) {
            DropMatrixElement element = iter.next();
            if (!openingStages.contains(element.getStageId()))
                iter.remove();
        }
    }

    private void filterStages(List<DropMatrixElement> elements, String stageFilter) {
        Set<String> filters = extractFilters(stageFilter);
        if (filters.isEmpty())
            return;
        Iterator<DropMatrixElement> iter = elements.iterator();
        while (iter.hasNext()) {
            DropMatrixElement element = iter.next();
            if (!filters.contains(element.getStageId()))
                iter.remove();
        }
    }

    private void filterItems(List<DropMatrixElement> elements, String itemFilter) {
        Set<String> filters = extractFilters(itemFilter);
        if (filters.isEmpty())
            return;
        Iterator<DropMatrixElement> iter = elements.iterator();
        while (iter.hasNext()) {
            DropMatrixElement element = iter.next();
            if (!filters.contains(element.getItemId()))
                iter.remove();
        }
    }

    private Set<String> extractFilters(String filterStr) {
        String[] splitted = filterStr.split(",");
        return Arrays.asList(splitted).stream().map(String::trim).collect(Collectors.toSet());
    }

}
