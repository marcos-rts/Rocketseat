package com.rocketseat.createurlshortner; 
// Define o pacote onde a classe está localizada. Isso ajuda a organizar o código em módulos.

import java.util.*; 
// Importa todas as classes do pacote java.util, incluindo Map, HashMap e UUID.

import com.amazonaws.services.lambda.runtime.Context; 
// Importa a interface Context para interagir com o ambiente de execução da AWS Lambda.

import com.amazonaws.services.lambda.runtime.RequestHandler; 
// Importa a interface RequestHandler, que deve ser implementada para definir o comportamento de uma função Lambda.

import com.fasterxml.jackson.core.JsonProcessingException; 
// Importa a exceção que pode ser lançada durante o processamento de JSON.

import com.fasterxml.jackson.databind.ObjectMapper; 
// Importa o ObjectMapper, usado para converter objetos Java em JSON e vice-versa.

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> { 
    // Define a classe principal que implementa a interface RequestHandler.
    // O tipo genérico `Map<String, Object>` representa o tipo da entrada da Lambda.
    // O tipo genérico `Map<String, String>` representa o tipo da saída da Lambda.

    private final ObjectMapper objectMapper = new ObjectMapper(); 
    // Cria uma instância do ObjectMapper para lidar com a conversão entre JSON e objetos Java.

    private final S3Client s3Client = S3Client.builder().build();

    @Override 
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) { 
        // Sobrescreve o método `handleRequest`, onde a lógica da função Lambda é implementada.
        // `input` contém os dados passados para a função Lambda.
        // `context` fornece informações sobre o ambiente de execução.

        // Recebe o corpo da requisição (supõe-se que esteja no formato JSON).
        String body = input.get("body").toString();

        // Declara um mapa para armazenar os dados extraídos do corpo JSON.
        Map<String, String> bodyMap;
        try {
            // Converte o JSON recebido em um Map<String, String>.
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch (Exception exception) { 
            // Caso ocorra um erro na conversão, lança uma exceção com detalhes do erro.
            throw new RuntimeException("Error parsing Json body: " + exception.getMessage(), exception);
        }

        // Extrai valores específicos do mapa gerado a partir do JSON.
        String originalUrl = bodyMap.get("origunalUrl"); 
        // Obtém o valor associado à chave "origunalUrl" (nota: há um erro de digitação aqui).
        String expirationTime = bodyMap.get("expirationTime"); 
        // Obtém o valor associado à chave "expirationTime".

        long expirationTimeInSecond = Long.parseLong(expirationTime) * 3600;

        // Gera um código curto aleatório usando UUID (Universal Unique Identifier).
        // O código é composto pelos 8 primeiros caracteres do UUID gerado.
        String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

        UrlData urlData =  new UrlData(originalUrl, expirationTimeInSecond);

        try {
            String urlDataJson = objectMapper.writeValueAsString(urlData);

            PutObjectRequest request = PutObjectRequest.builder()
            .bucket("mxt-url-shotener-storage")
            .key(shortUrlCode + ".json")
            .build();

            s3Client.putObject(request, RequestBody.fromString(urlDataJson));

        } catch (Exception exception) {
            throw new RuntimeException("Erro saving URL data to S3" + exception.getMessage(), exception); 
        }

        // Cria um mapa para armazenar a resposta que será retornada.
        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCode); 
        // Adiciona o código curto gerado à resposta com a chave "code".

        return response; 
        // Retorna o mapa de resposta contendo o código curto gerado.
    }
}
