package com.betrybe.agrix.evaluation;

import static com.betrybe.agrix.evaluation.util.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.betrybe.agrix.evaluation.mock.FarmFixtures;
import com.betrybe.agrix.evaluation.mock.MockFarm;
import com.betrybe.agrix.evaluation.util.SimpleResultHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Req 01")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
class FarmManagementTest {

  MockMvc mockMvc;

  @Autowired
  ObjectMapper jsonMapper = new ObjectMapper();

  @BeforeEach
  public void setup(WebApplicationContext wac) {
    // We need this to make sure the response body is in UTF-8,
    // since we're testing raw strings
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();
  }

  @Test
  @DisplayName("1- Migre seu código da Fase A para este projeto (Fase B)")
  void testFarmCreation() throws Exception {
    MockFarm farm = new MockFarm(FarmFixtures.farm1);

    MockFarm result = performFarmCreation(farm);

    assertNotNull(result.get("id"), "A resposta deve incluir o ID da fazenda criada");

    // Add id so that comparison makes sense
    farm.put("id", result.get("id"));

    assertEquals(
        farm,
        result
    );
  }

  private MockFarm performFarmCreation(MockFarm farm) throws Exception {
    String responseContent = mockMvc.perform(post("/farms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJson(farm)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    return jsonMapper.readValue(responseContent, MockFarm.class);
  }
}
