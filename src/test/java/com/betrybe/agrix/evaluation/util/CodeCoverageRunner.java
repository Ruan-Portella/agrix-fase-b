package com.betrybe.agrix.evaluation.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

public class CodeCoverageRunner {

  private final String buildDir;
  private final String profileName;

  public CodeCoverageRunner(String buildDir) {
    this(buildDir, null);
  }

  public CodeCoverageRunner(String buildDir, String profileName) {
    this.buildDir = buildDir;
    this.profileName = profileName;
  }
  public double run() {
    String reportPath = String.format("%s/site/jacoco/jacoco.xml", buildDir);
    String outFile = String.format("%s.log", buildDir);

    String[] mvnCmd = new String[]{
        /*
         * Start by unsetting the MAVEN_CONFIG env var because it creates a conflict
         * between the Docker image in Github Actions and the mvnw wrapper.
         * For more info, see: https://issues.jenkins.io/browse/JENKINS-47890
         */
        "unset MAVEN_CONFIG &&",
        "./mvnw clean test jacoco:report",  // Run tests
        "-DuseTestsForCoverage=true",  // Include evaluation and solution tests, but exclude coverage test (like this one) or other unrelated
        "-DcoverageBuildDir=" + buildDir,  // Use different dir to build
        profileName != null ? "-D%s=true".formatted(profileName) : "",
        "--log-file " + outFile, // Save output to file
    };

    try {
      String[] cmd = new String[]{
          "sh", "-c",
          String.join(" ", mvnCmd)
      };
      Process p =
          Runtime.getRuntime().exec(cmd);

      if (p.waitFor() != 0) {
        fail(
            "Erro ao executar teste de cobertura, verifique se os outros testes estão passando, "
                + "incluindo o linter! Arquivo de log: " + outFile);
      }

      File file = new File(reportPath);
      Map<String, Object> result = new XmlParser().parseToMap(file);
      List<Map<String, String>> evaluations = (List<Map<String, String>>) result.get("counters");

      Map<String, String> sample = evaluations.stream()
          .filter(eval -> eval.get("type").equals("LINE")).collect(Collectors.toList()).get(0);

      return Double.parseDouble(sample.get("percentage"));
    } catch (IOException e) {
      fail("Arquivo de cobertura não encontrado. Faça os testes passarem antes!");
    } catch (XPathExpressionException | ParserConfigurationException | InterruptedException |
             SAXException e) {
      throw new RuntimeException(e);
    }
    return 0;
  }

}
