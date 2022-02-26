package com.accenture.correlationcoeffcalculator.service;

import com.accenture.correlationcoeffcalculator.constants.CorrCoeffCalcConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Correlation finder service to calculate it's value using Pearson's method from apache common math3 library
 */
@Service
public class CorrCoeffCalcService {

    private static final Logger logger = LoggerFactory.getLogger(CorrCoeffCalcService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * restBaseUrl from the application properties file
     */
    @Value("${rest.base.url}")
    private String restBaseUrl;

    /**
     * Method to determine the correlation coefficient value as per the continent provided
     *
     * @param continent
     * @return a double value which can either be a code or the correlation coefficient from the pearson's method.
     * @throws JsonProcessingException
     */
    public Double calculateByContinent(final String continent) throws JsonProcessingException {
        logger.info("Calculating Correlation Coefficient for the continent {} ", continent);
        double result = CorrCoeffCalcConstant.MAGIC_NUMBER_99;
        try {
            result = computePearsonCorrelation(continent, CorrCoeffCalcConstant.CHARACTER_C);
        } catch (Exception exception) {
            logger.error("Exception occurred while calculating the correlation coefficient for the input {} ", continent, exception);
            result = CorrCoeffCalcConstant.INTERNAL_SERVER_ERROR;
        }
        return result;
    }

    public Double calculateByCountry(final String country) throws JsonProcessingException {
        logger.info("Calculating Correlation Coefficient for the continent {} ", country);
        double result = CorrCoeffCalcConstant.MAGIC_NUMBER_99;
        try {
            result = computePearsonCorrelation(country, CorrCoeffCalcConstant.CHARACTER_A);
        } catch (Exception exception) {
            logger.error("Exception occurred while calculating the correlation coefficient for the input {} ", country, exception);
            result = CorrCoeffCalcConstant.INTERNAL_SERVER_ERROR;
        }
        return result;
    }

    private double computePearsonCorrelation(final String input, final char option) throws JsonProcessingException {
        double result = CorrCoeffCalcConstant.MAGIC_NUMBER_99;
        Map<String, Double> vaccinatePercentageMap = getVaccinatedPercentageMap(input, option);
        Map<String, Double> resultingDeathPercentageMap = new HashMap<>(getDeathPercentageMap(input, option));
        resultingDeathPercentageMap.keySet().retainAll(vaccinatePercentageMap.keySet());
        double[] vaccinatePercentage = vaccinatePercentageMap.values().stream().mapToDouble(v -> v).toArray();
        double[] resultingDeathPercentage = resultingDeathPercentageMap.values().stream().mapToDouble(value -> value).toArray();
        if (checkCorrelationCondition(vaccinatePercentage, resultingDeathPercentage))
            result = new PearsonsCorrelation().correlation(resultingDeathPercentage, vaccinatePercentage);
        else {
            logger.info("calculated array mismatch with length {}, {}", vaccinatePercentage.length, resultingDeathPercentage.length);
        }
        return result;
    }

    private boolean checkCorrelationCondition(final double[] array1, final double[] array2) {
        boolean result = false;
        if (null != array1 && null != array2 && array1.length > 0 && array1.length == array2.length)
            result = true;
        return result;
    }

    private Map<String, Double> getDeathPercentageMap(final String input, final Character option) throws JsonProcessingException {
        Map<String, Double> deathPercentageMap = new HashMap<String, Double>();
        String API_END_POINT = option == CorrCoeffCalcConstant.CHARACTER_A ? CorrCoeffCalcConstant.CASES_API_COUNTRY : CorrCoeffCalcConstant.CASES_API_CONTINENT;
        ResponseEntity<String> response = restTemplate.getForEntity(restBaseUrl + API_END_POINT + input, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            root.iterator().forEachRemaining(jsonNode -> {
                if (jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL) != null && jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_COUNTRY) != null) {
                    String country = jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_COUNTRY).asText();
                    double deaths = jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_DEATHS).asDouble();
                    double population = jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_POPULATION).asDouble();
                    deathPercentageMap.put(country, (deaths / population));
                }
            });
        }
        return deathPercentageMap;
    }


    private Map<String, Double> getVaccinatedPercentageMap(final String input, final Character option) throws JsonProcessingException {
        Map<String, Double> vaccinatedPercentageMap = new HashMap<>();
        String API_END_POINT = option == CorrCoeffCalcConstant.CHARACTER_A ? CorrCoeffCalcConstant.VACCINES_API_COUNTRY : CorrCoeffCalcConstant.VACCINES_API_CONTINENT;
        ResponseEntity<String> response = restTemplate.getForEntity(restBaseUrl + API_END_POINT + input, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            root.iterator().forEachRemaining(jsonNode -> {
                System.out.println(jsonNode.toPrettyString());
                if (jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL) != null && jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_COUNTRY) != null)
                {
                    String country = jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_COUNTRY).asText();
                    double people_vaccinated = jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_PEOPLE_VACCINATED).asDouble();
                    double population = jsonNode.get(CorrCoeffCalcConstant.PROPERTY_ALL).get(CorrCoeffCalcConstant.PROPERTY_POPULATION).asDouble();
                    vaccinatedPercentageMap.put(country, (people_vaccinated / population));
                }
            });
        }
        return vaccinatedPercentageMap;
    }

}
