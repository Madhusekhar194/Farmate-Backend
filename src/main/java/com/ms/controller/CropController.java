package com.ms.controller;

import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.LoadingModelEvaluatorBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class CropController {

    private final ModelEvaluator<?> modelEvaluator;

    public CropController() throws Exception {
        // Load PMML from resources folder
        InputStream is = getClass().getResourceAsStream("/CropModel.pmml");

        if (is == null) {
            throw new RuntimeException("PMML model file not found! Make sure CropModel.pmml is in src/main/resources");
        }

        // Load and verify the model
        this.modelEvaluator = new LoadingModelEvaluatorBuilder()
                .load(is)
                .build();
        this.modelEvaluator.verify();
    }

    @PostMapping(value = "/predict", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> predict(@RequestBody Map<String, Object> inputData) {

        // Prepare input arguments
        Map<String, Object> arguments = new HashMap<>();
        for (InputField inputField : modelEvaluator.getInputFields()) {
        	String name = inputField.getName().toString(); // ✅ Correct
            Object rawValue = inputData.get(name);
            FieldValue preparedValue = inputField.prepare(rawValue);
            arguments.put(name, preparedValue.getValue());
        }

        // Evaluate the model
        Map<String, ?> results = modelEvaluator.evaluate(arguments);

        // Get prediction (assuming single target field)
        Object prediction = results.values().iterator().next();
        String recommendedCrop = prediction.toString();

        Map<String, String> response = new HashMap<>();
        response.put("recommendedCrop", recommendedCrop);
        return response;
    }
}
