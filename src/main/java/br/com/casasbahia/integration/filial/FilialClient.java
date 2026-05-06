package br.com.casasbahia.integration.filial;

import br.com.casasbahia.dto.FilialResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FilialClient {

    private final RestTemplate restTemplate;
    private final String filialBaseUrl;

    public FilialClient(RestTemplate restTemplate,
                        @Value("${integration.filial.url}") String filialBaseUrl) {
        this.restTemplate = restTemplate;
        this.filialBaseUrl = filialBaseUrl;
    }

    public FilialResponseDTO buscarPorId(String filialId) {
        try {
            return restTemplate.getForObject(
                    filialBaseUrl + "/filiais/" + filialId,
                    FilialResponseDTO.class
            );
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Erro ao buscar filial na API externa. FilialId=" + filialId,
                    ex
            );
        }
    }

}
