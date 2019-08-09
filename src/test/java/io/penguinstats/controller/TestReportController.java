package io.penguinstats.controller;

import io.penguinstats.config.TestConfig;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestReportController {

    private static String itemDropHashId;

    @Autowired
    MockMvc mockMvc;

    @AfterClass
    public static void cleanDB() {
        TestConfig.mongoTemplate.getCollection("user").deleteMany(new Document());
        TestConfig.mongoTemplate.getCollection("item_drop_v2").deleteMany(new Document());
    }

    @Test
    public void test01SaveSingleReport() throws Exception {
        String contentJson = "{\"stageId\":\"main_01-01\",\"furnitureNum\":0,\"drops\":[{\"itemId\":\"3003\",\"quantity\":35}],\"source\":\"penguin-stats.io\",\"version\":\"v1.4.6\"}";

        ResultActions result = mockMvc.perform(post("/api/report").cookie(new Cookie("userID", TestConfig.testUserID))
        .content(contentJson)).andExpect(status().isCreated());

        itemDropHashId = result.andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test02GetPersonalReportHistory() throws Exception {
        mockMvc.perform(get("/api/report/history")
                .cookie(new Cookie("userID", TestConfig.testUserID))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("page","0")
                .param("page_size", "50")
                .param("sort_by", "timestamp")
                .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].drops[0].itemId", is("3003")))
                .andExpect(jsonPath("$[0].drops[0].quantity", is(35)))
                .andExpect(jsonPath("$[0].isDeleted", is(false)))
                .andExpect(jsonPath("$[0].isReliable", is(false)))
                .andExpect(jsonPath("$[0].source", is("penguin-stats.io")))
                .andExpect(jsonPath("$[0].stageId", is("main_01-01")))
                .andExpect(jsonPath("$[0].times", is(1)))
                .andExpect(jsonPath("$[0].userID", is(TestConfig.testUserID)));
    }

    @Test
    public void test03RecallPersonalReport() throws Exception {
        mockMvc.perform(post("/api/report/recall")
                .cookie(new Cookie("userID", TestConfig.testUserID))
                .param("item_drop_hash_id", itemDropHashId))
                .andExpect(status().isOk());
    }
}
