package com.accenture.correlationcoeffcalculator.controller;

import com.accenture.correlationcoeffcalculator.service.CorrCoeffCalcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.function.DoubleBinaryOperator;

@RestController
@RequestMapping("v1/coeff")
public class CorrCoeffCalcController {

    private  static final Logger logger = LoggerFactory.getLogger(CorrCoeffCalcController.class);

    @Autowired
    private CorrCoeffCalcService corrCoeffCalcService;

    @GetMapping(value = "/calculate", params = "continent")
    public ResponseEntity<Object> calculateCoeffContinent(@PathParam("continent") String continent ) {
        logger.info("calculateCoeffContinent for Input {}", continent);
        ResponseEntity<Object> response = null;
        if ( null != continent && !continent.isEmpty()) {
            double result = 0.0;
            try {
                result = corrCoeffCalcService.calculateByContinent(continent);
                response = getResponse(result) ;
            } catch (JsonProcessingException e) {
                logger.error("Exception occurred ");
                response = new ResponseEntity<>("Oops, something went wrong, Please try again!", HttpStatus.ACCEPTED);
            }
        } else {
            response = new ResponseEntity<>("value cannot be empty, please enter a valid input", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping(value = "/calculate", params = "country")
    public ResponseEntity<Object> calculateCoeffCountry(@PathParam("country") String country ) {
        logger.info("calculateCoeffContinent for Input {}", country);
        ResponseEntity<Object> response = null;
        if ( null != country && !country.isEmpty()) {
            double result = 0.0;
            try {
                result = corrCoeffCalcService.calculateByCountry(country);
                response = getResponse(result) ;
            } catch (JsonProcessingException e) {
                logger.error("Exception occurred ");
                response = new ResponseEntity<>("Oops, something went wrong, Please try again!", HttpStatus.ACCEPTED);
            }
        } else {
            response = new ResponseEntity<>("value cannot be empty, please enter a valid input", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    private ResponseEntity<Object> getResponse(final double result) {
        ResponseEntity<Object> response;
        if ( result == 500.0 || result == 99.0 ) {
            response = new ResponseEntity<>("Something went wrong, please connect with customer support", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(result,HttpStatus.OK);
        }
        return response;
    }
}
