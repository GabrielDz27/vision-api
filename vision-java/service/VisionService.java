import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.io.File;

@Service
public class VisionService {

    private final String PYTHON_URL = "http://localhost:8000/processar";

    public VisionResponseDTO analisarImagem(File imagemFile) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Prepara o corpo da requisição como "multipart/form-data"
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(imagemFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // 2. Envia para o Python
            ResponseEntity<VisionResponseDTO> response = restTemplate.postForEntity(
                PYTHON_URL, requestEntity, VisionResponseDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao conectar com a API Python: " + e.getMessage());
            return null;
        }
    }
}