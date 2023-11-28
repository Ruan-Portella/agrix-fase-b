package com.betrybe.agrix.evaluation;

import static com.betrybe.agrix.evaluation.util.TestHelpers.objectToJson;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.betrybe.agrix.evaluation.mock.CropFixtures;
import com.betrybe.agrix.evaluation.mock.FarmFixtures;
import com.betrybe.agrix.evaluation.mock.FertilizerFixtures;
import com.betrybe.agrix.evaluation.mock.MockCrop;
import com.betrybe.agrix.evaluation.mock.MockFarm;
import com.betrybe.agrix.evaluation.mock.MockFertilizer;
import com.betrybe.agrix.evaluation.util.SimpleResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@DisplayName("Req 08-12")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
class FertilizerManagementTest {

  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup(WebApplicationContext wac) throws Exception {
    // We need this to make sure the response body is in UTF-8,
    // since we're testing raw strings
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();
  }

  @Test
  @DisplayName("8- Crie a rota POST /fertilizers")
  void testFertilizerCreation() throws Exception {
    MockFertilizer fertilizer = FertilizerFixtures.fertilizer1;

    MockFertilizer savedFertilizer = performFertilizarCreation(fertilizer);

    assertNotNull(savedFertilizer.get("id"), "A resposta deve incluir o ID do fertilizante criado");

    // Add id so that comparison makes sense
    MockFertilizer expectedFertilizer = fertilizer.clone();
    expectedFertilizer.put("id", savedFertilizer.get("id"));

    assertEquals(
        expectedFertilizer,
        savedFertilizer
    );
  }

  @Test
  @DisplayName("9- Crie a rota GET /fertilizers")
  void testGetAllFertilizers() throws Exception {
    List<MockFertilizer> fertilizers = List.of(
        FertilizerFixtures.fertilizer1,
        FertilizerFixtures.fertilizer2,
        FertilizerFixtures.fertilizer3
    );

    Set<MockFertilizer> expectedFertilizers = new HashSet<>();

    for (MockFertilizer fertilizer : fertilizers) {
      expectedFertilizers.add(
          performFertilizarCreation(fertilizer)
      );
    }

    String responseContent = mockMvc.perform(get("/fertilizers")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    Set<Map<String, Object>> returnedFertilizers = Set.copyOf(
        objectMapper.readValue(responseContent,
            new TypeReference<>() {
            })
    );

    assertEquals(
        expectedFertilizers,
        returnedFertilizers
    );
  }

  @Test
  @DisplayName("10- Crie a rota GET /fertilizers/{id}")
  void testGetFertilizer() throws Exception {
    testGetFertilizerSuccess();
    testGetFertilizerNotFound();
  }

  void testGetFertilizerSuccess() throws Exception {
    MockFertilizer savedFertilizer = performFertilizarCreation(FertilizerFixtures.fertilizer2);

    // Get fertilizer to check if returned correctly
    String getUrl = "/fertilizers/%s".formatted(savedFertilizer.get("id"));
    String responseContent = mockMvc.perform(get(getUrl)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    MockFertilizer expectedFertilizer = savedFertilizer.clone();
    MockFertilizer returnedFertilizer = objectMapper.readValue(responseContent,
        MockFertilizer.class);

    assertEquals(
        expectedFertilizer,
        returnedFertilizer
    );
  }

  void testGetFertilizerNotFound() throws Exception {
    mockMvc.perform(get("/fertilizers/999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Fertilizante não encontrado!")));
  }

  @Test
  @DisplayName("11- Crie a rota POST /crops/{cropId}/fertilizers/{fertilizerId}")
  void testAddFertilizerToCrop() throws Exception {
    testAddFertilizerToCropSucess();
    testAddFertilizerToCropButCropNotFound();
    testAddFertilizerToCropButFertilizerNotFound();
  }

  void testAddFertilizerToCropSucess() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm1);
    MockCrop crop = performCropCreation(farm, CropFixtures.crop1);
    MockFertilizer fertilizer = performFertilizarCreation(FertilizerFixtures.fertilizer1);

    // Add fertilizer to crop
    String postUrl = "/crops/%s/fertilizers/%s".formatted(crop.get("id"), fertilizer.get("id"));
    mockMvc.perform(post(postUrl))
        .andExpect(status().isCreated())
        .andExpect(content().string(
            containsString("Fertilizante e plantação associados com sucesso!")));
  }

  void testAddFertilizerToCropButFertilizerNotFound() throws Exception {
    MockFertilizer fertilizer = performFertilizarCreation(FertilizerFixtures.fertilizer1);

    // Add fertilizer to crop
    String postUrl = "/crops/999/fertilizers/%s".formatted(fertilizer.get("id"));
    mockMvc.perform(post(postUrl))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Plantação não encontrada!")));
  }

  void testAddFertilizerToCropButCropNotFound() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm1);
    MockCrop crop = performCropCreation(farm, CropFixtures.crop1);

    // Add fertilizer to crop
    String postUrl = "/crops/%s/fertilizers/999".formatted(crop.get("id"));
    mockMvc.perform(post(postUrl))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Fertilizante não encontrado!")));
  }

  @Test
  @DisplayName("12- Crie a rota GET /crops/{cropId}/fertilizers")
  void testGetCropFertilizers() throws Exception {
    testGetCropFertilizersSuccess();
    testGetCropFertilizersCropNotFound();
    testGetCropFertilizersEmpty();
  }

  void testGetCropFertilizersSuccess() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm1);
    MockCrop crop = performCropCreation(farm, CropFixtures.crop1);

    List<MockFertilizer> fertilizers = List.of(
        FertilizerFixtures.fertilizer1,
        FertilizerFixtures.fertilizer2
    );

    // Create fertilizers and associate with crop
    Set<MockFertilizer> expectedFertilizers = new HashSet<>();
    for (MockFertilizer fertilizer : fertilizers) {
      MockFertilizer savedFertilizer = performFertilizarCreation(fertilizer);
      expectedFertilizers.add(savedFertilizer);

      String postUrl = "/crops/%s/fertilizers/%s".formatted(
          crop.get("id"), savedFertilizer.get("id"));

      mockMvc.perform(post(postUrl))
          .andExpect(status().isCreated());
    }

    // Get fertilizers for crop, to check it returns correctly
    String getUrl = "/crops/%s/fertilizers".formatted(crop.get("id"));
    String responseContent = mockMvc.perform(get(getUrl).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    Set<MockFertilizer> returnedFertilizers = Set.copyOf(
        objectMapper.readValue(responseContent,
            new TypeReference<>() {
            })
    );

    assertEquals(
        expectedFertilizers,
        returnedFertilizers
    );
  }

  void testGetCropFertilizersCropNotFound() throws Exception {
    mockMvc.perform(get("/crops/999/fertilizers")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Plantação não encontrada!")));
  }

  void testGetCropFertilizersEmpty() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm1);
    MockCrop crop = performCropCreation(farm, CropFixtures.crop1);

    String url = "/crops/%s/fertilizers".formatted(crop.get("id"));

    // Get fertilizers for crop, to check if it returns an empty list
    mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isEmpty());
  }

  /**
   * Auxiliar method to create crops.
   */
  private MockFarm performFarmCreation(MockFarm farm) throws Exception {
    String url = "/farms";

    String responseContent =
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(farm)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    return objectMapper.readValue(responseContent, MockFarm.class);
  }

  /**
   * Auxiliar method to create crops.
   */
  private MockCrop performCropCreation(MockFarm farm, MockCrop crop) throws Exception {
    String url = "/farms/%s/crops".formatted(farm.get("id"));

    String responseContent =
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(crop)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    return objectMapper.readValue(responseContent, MockCrop.class);
  }

  /**
   * Auxiliar method to create fertilizers.
   */
  private MockFertilizer performFertilizarCreation(MockFertilizer fertilizer) throws Exception {
    String url = "/fertilizers";

    String responseContent =
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(fertilizer)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    return objectMapper.readValue(responseContent, MockFertilizer.class);
  }
}
