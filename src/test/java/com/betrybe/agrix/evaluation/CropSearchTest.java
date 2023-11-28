package com.betrybe.agrix.evaluation;

import static com.betrybe.agrix.evaluation.util.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.betrybe.agrix.evaluation.mock.CropFixtures;
import com.betrybe.agrix.evaluation.mock.FarmFixtures;
import com.betrybe.agrix.evaluation.mock.MockCrop;
import com.betrybe.agrix.evaluation.mock.MockFarm;
import com.betrybe.agrix.evaluation.util.SimpleResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
@DisplayName("Req 07")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class CropSearchTest {

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
  @DisplayName("7- Crie a rota GET /crops/search para busca de plantações")
  void testSearchCropsByHarvestDate() throws Exception {
    // Create the farms and crops
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

    // Set a differente date for each crop
    LocalDate harvestDate = LocalDate.of(2023, 10, 19);
    LocalDate firstDate = LocalDate.from(harvestDate);
    LocalDate lastDate = LocalDate.from(harvestDate);

    for (Map.Entry<MockFarm, List<MockCrop>> entry : farmsCrops.entrySet()) {
      MockFarm farm = performFarmCreation(entry.getKey());

      for (MockCrop mockCrop : entry.getValue()) {
        MockCrop crop = mockCrop.clone();
        crop.put("harvestDate", harvestDate.toString());

        MockCrop expectedCrop = performCropCreation(farm, crop);
        expectedCrops.add(expectedCrop);

        lastDate = LocalDate.from(harvestDate);
        harvestDate = harvestDate.plusMonths(1);
      }
    }

    // Create a date range that excludes some of thr crops
    LocalDate startDate = firstDate.plusDays(5);
    LocalDate endDate = lastDate.minusDays(5);

    expectedCrops = expectedCrops.stream().filter(
        d -> isBetweenInclusive(LocalDate.parse((String) d.get("harvestDate")), startDate, endDate)
    ).collect(Collectors.toSet());

    // Test the search
    String searchUrl = "/crops/search?start=%s&end=%s".formatted(startDate, endDate);

    String responseContent = mockMvc.perform(get(searchUrl).accept(MediaType.APPLICATION_JSON))
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

  private boolean isBetweenInclusive(LocalDate date, LocalDate start, LocalDate end) {
    return
        (date.isAfter(start) && date.isBefore(end))
            || date.isEqual(start)
            || date.isEqual(end);
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