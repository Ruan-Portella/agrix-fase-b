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
import com.betrybe.agrix.evaluation.mock.MockCrop;
import com.betrybe.agrix.evaluation.mock.MockFarm;
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
@DisplayName("Req 03-06")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class CropManagementTest {

  MockMvc mockMvc;

  @Autowired
  WebApplicationContext wac;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() throws Exception {
    // We need this to make sure the response body is in UTF-8,
    // since we're testing raw strings
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();
  }

  @Test
  @DisplayName("3- Ajuste (ou crie) a rota POST /farms/{farmId}/crops para utilizar datas")
  void testCropCreation() throws Exception {
    testCropCreationSuccess();
    testCropCreationFarmNotFound();
  }

  void testCropCreationSuccess() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm1);
    MockCrop crop = CropFixtures.crop1;

    MockCrop savedCrop = performCropCreation(farm, crop);

    assertNotNull(savedCrop.get("id"), "A resposta deve incluir o ID da plantação criada");

    // Add id so that comparison makes sense
    MockCrop expectedCrop = new MockCrop(crop);
    expectedCrop.put("id", savedCrop.get("id"));
    expectedCrop.put("farmId", farm.get("id"));

    assertEquals(
        expectedCrop,
        savedCrop
    );
  }

  void testCropCreationFarmNotFound() throws Exception {
    MockCrop crop = CropFixtures.crop1;

    String url = "/farms/99999/crops";

    mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJson(crop)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Fazenda não encontrada!")));
  }

  @Test
  @DisplayName("4- Ajuste (ou crie) a rota GET /farms/{farmId}/crops para utilizar datas")
  void testGetFarmCrops() throws Exception {
    testGetFarmCropsSuccess();
    testGetFarmCropsEmpty();
    testGetFarmCropsFarmNotFound();
  }

  void testGetFarmCropsEmpty() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm3);

    String url = "/farms/%s/crops".formatted(farm.get("id"));

    // Get crops for farm, to check if it returns an empty list
    mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isEmpty());
  }

  void testGetFarmCropsFarmNotFound() throws Exception {
    // Get crops for farm, to check it returns correctly
    mockMvc.perform(get("/farms/999/crops")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Fazenda não encontrada!")));
  }

  void testGetFarmCropsSuccess() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm2);

    Set<MockCrop> crops = Set.of(
        CropFixtures.crop3,
        CropFixtures.crop4,
        CropFixtures.crop5
    );

    String url = "/farms/%s/crops".formatted(farm.get("id"));

    // Create crops
    Set<MockCrop> expectedCrops = new HashSet<>();

    for (MockCrop crop : crops) {
      MockCrop savedCrop = performCropCreation(farm, crop);

      expectedCrops.add(savedCrop);
    }

    // Get crops for farm, to check it returns correctly
    String responseContent = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    Set<MockCrop> returnedCrops = objectMapper.readValue(responseContent,
        new TypeReference<>() {
        });

    assertEquals(
        expectedCrops,
        returnedCrops
    );
  }

  @Test
  @DisplayName("5- Ajuste (ou crie) a rota GET /crops para utilizar datas")
  void testGetAllCrops() throws Exception {
    Map<MockFarm, List<MockCrop>> farmsCrops = Map.of(
        FarmFixtures.farm1, List.of(
            CropFixtures.crop1,
            CropFixtures.crop2
        ),
        FarmFixtures.farm2, List.of(
            CropFixtures.crop3,
            CropFixtures.crop4,
            CropFixtures.crop5
        )
    );

    Set<MockCrop> expectedCrops = new HashSet<>();

    for (Map.Entry<MockFarm, List<MockCrop>> entry : farmsCrops.entrySet()) {
      MockFarm farm = performFarmCreation(entry.getKey());

      for (MockCrop crop : entry.getValue()) {
        MockCrop expectedCrop = performCropCreation(farm, crop);
        expectedCrops.add(expectedCrop);
      }
    }

    String responseContent = mockMvc.perform(get("/crops").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    Set<MockCrop> returnedCrops = Set.copyOf(
        objectMapper.readValue(responseContent,
            new TypeReference<>() {
            })
    );

    assertEquals(
        expectedCrops,
        returnedCrops
    );
  }

  @Test
  @DisplayName("6- Ajuste (ou crie) a rota GET /crops/{id} para utilizar datas")
  void testGetCrop() throws Exception {
    testGetCropSuccess();
    testGetCropNotFound();
  }

  void testGetCropSuccess() throws Exception {
    MockFarm farm = performFarmCreation(FarmFixtures.farm1);
    MockCrop crop = performCropCreation(farm, CropFixtures.crop2);

    // Get crop to check if returned correctly
    String getUrl = "/crops/%s".formatted(crop.get("id"));
    String responseContent = mockMvc.perform(get(getUrl)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    MockCrop returnedCrop = objectMapper.readValue(responseContent, MockCrop.class);

    assertEquals(
        crop,
        returnedCrop
    );
  }

  void testGetCropNotFound() throws Exception {
    mockMvc.perform(get("/crops/99999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Plantação não encontrada!")));
  }

  /**
   * Auxiliar method to create farms.
   */
  private MockFarm performFarmCreation(MockFarm farm) throws Exception {
    String responseContent = mockMvc.perform(post("/farms")
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
}