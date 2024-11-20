package com.rocketseat.createurlshortner;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        // Recebe o Objeto
        String body = input.get("body").toString();

        // Variavel do tipo Chave String e Valor String
        Map<String, String> bodyMap;
        try {
            // Vai transformar o Body em um Mapp
            bodyMap = objectMapper.readValue(body, Map.class);

        } catch (Exception exception) {
            throw new RuntimeException("Error parsing Json body: " + exception.getMessage(), exception);
        }

        String originalUrl = bodyMap.get("origunalUrl");
        String expirationTime = bodyMap.get("expirationMap");

        return null;
    }
}