package com.betrybe.agrix.evaluation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.betrybe.agrix.evaluation.util.CodeCoverageRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Req 02")
@SpringBootTest
@ActiveProfiles("test")
public class CoverageValidationTest {

  private static final Logger log = LoggerFactory.getLogger(CoverageValidationTest.class);

  @Test
  @DisplayName("2- Escreva testes com cobertura mínima de 80% das linhas da classe PersonService")
  public void testCollectionTypeCoverage() {
    CodeCoverageRunner codeCoverage =
        new CodeCoverageRunner("target-coverage-req-02", "personServiceCoverage");

    double minExpectedCoverage = 80;
    double actualCoverage = codeCoverage.run();

    checkCodeCoverage(minExpectedCoverage, actualCoverage);
  }

  private void checkCodeCoverage(double minExpected, double actual) {
    assertTrue(
        actual >= minExpected,
        String.format(
            "Cobertura atual é de %.1f%%,"
                + " mas deveria ser de no mínimo %.1f%%",
            actual, minExpected)
    );

    log.info(
            String.format("Cobertura de código em %.1f%% (mínimo de %.1f%%)", actual, minExpected)
    );
  }
}