package com.seek.camunda.delegates;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate; // O WebClient si usas reactivo

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component("actualizarProductoDelegate") // Nombre usado en la expresión del Service Task
public class ActualizarProductoDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ActualizarProductoDelegate.class);

    @Autowired
    private RestTemplate restTemplate; // Inyecta un RestTemplate (debes configurarlo como Bean)

    @Value("${api.catalogo.url}") // Lee la URL de application.properties/yml
    private String apiUrlBase;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String productId = (String) execution.getVariable("productId");
        // Usar getVariableTyped para evitar ClassCastException si el tipo no es el
        // esperado
        Long newQuantity = execution.getVariableTyped("newQuantity", false) != null
                ? (Long) execution.getVariable("newQuantity")
                : null;
        Double newPrice = execution.getVariableTyped("newPrice", false) != null
                ? (Double) execution.getVariable("newPrice")
                : null;

        log.info("Iniciando actualización para producto ID: {}. Cantidad: {}, Precio: {}", productId, newQuantity,
                newPrice);

        // Construir URL final
        String apiUrl = apiUrlBase + "/products/" + productId;

        // Construir cuerpo de la solicitud (solo incluir campos si tienen valor)
        Map<String, Object> requestBody = new HashMap<>();
        if (newQuantity != null) {
            requestBody.put("quantity", newQuantity);
        }
        if (newPrice != null) {
            requestBody.put("price", newPrice);
        }

        if (requestBody.isEmpty()) {
            log.warn("No se proporcionó ni cantidad ni precio para actualizar el producto ID: {}", productId);
            // Decide cómo manejar esto: éxito sin hacer nada o fallo? Vamos a considerarlo
            // fallo leve.
            execution.setVariable("updateStatus", "FAILED");
            execution.setVariable("errorMessage", "No se proporcionaron datos para actualizar.");
            // Opcional: lanzar BpmnError si esto no debería pasar (la validación debería
            // haberlo prevenido)
            // throw new BpmnError("UPDATE_NO_DATA", "No se proporcionaron datos para
            // actualizar.");
            return;
        }

        // Configurar Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Añadir otros headers si son necesarios (ej. Authorization)
        // headers.setBearerAuth("TU_TOKEN_JWT");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Realizar la llamada API (usando PATCH, o PUT si reemplaza todo)
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.PATCH, // o HttpMethod.PUT
                    requestEntity,
                    String.class // Tipo de respuesta esperado (puede ser un DTO si la API devuelve algo útil)
            );

            // Evaluar la respuesta
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("API call successful for product ID: {}. Status: {}", productId, response.getStatusCode());
                execution.setVariable("updateStatus", "SUCCESS");
                execution.removeVariable("errorMessage"); // Limpiar errores previos
            } else {
                // Esto no debería ocurrir con RestTemplate si no lanza excepción, pero por si
                // acaso
                log.error("API call failed for product ID: {}. Status: {}, Body: {}", productId,
                        response.getStatusCode(), response.getBody());
                execution.setVariable("updateStatus", "FAILED");
                execution.setVariable("errorMessage", "API devolvió estado no exitoso: " + response.getStatusCode());
                // Opcional: lanzar BpmnError
                // throw new BpmnError("API_FAILURE", "API devolvió estado no exitoso: " +
                // response.getStatusCode());
            }

        } catch (HttpStatusCodeException e) {
            // Captura errores HTTP específicos (4xx, 5xx)
            log.error("API call failed for product ID: {}. Status: {}. Response: {}", productId, e.getStatusCode(),
                    e.getResponseBodyAsString(), e);
            execution.setVariable("updateStatus", "FAILED");
            String apiErrorMessage = "Error API: " + e.getStatusCode();
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                apiErrorMessage += " (Producto no encontrado)";
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                apiErrorMessage += " (Datos inválidos: " + e.getResponseBodyAsString() + ")";
            } else {
                apiErrorMessage += " (" + e.getResponseBodyAsString() + ")";
            }
            execution.setVariable("errorMessage", apiErrorMessage);
            // Opcional: lanzar BpmnError específico según el código de estado
            // throw new BpmnError("API_FAILURE_" + e.getStatusCode(), apiErrorMessage);

        } catch (Exception e) {
            // Captura otros errores (ej. conexión, timeout)
            log.error("Error inesperado durante la llamada API para producto ID: {}", productId, e);
            execution.setVariable("updateStatus", "FAILED");
            execution.setVariable("errorMessage", "Error de conexión o inesperado al llamar la API: " + e.getMessage());
            // Lanzar un BpmnError aquí es recomendable para errores técnicos graves
            throw new BpmnError("API_CONNECTION_ERROR",
                    "Error de conexión o inesperado al llamar la API: " + e.getMessage());
        }
    }
}