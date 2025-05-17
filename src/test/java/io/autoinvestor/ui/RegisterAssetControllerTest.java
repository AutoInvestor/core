package io.autoinvestor.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.autoinvestor.domain.AssetRepository;
import io.autoinvestor.infrastructure.repositories.InMemoryAssetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(InMemoryAssetRepository.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RegisterAssetControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired AssetRepository repo;

    private static final String URL = "/register";

    @Test
    @DisplayName("Happy path → 200 OK and asset persisted")
    void registersNewAsset() throws Exception {
        var body = mapper.writeValueAsString(new RegisterAssetDTO("XNYS", "AAPL", "Apple Inc."));

        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        boolean exists = repo.exists("XNYS", "AAPL");
        assert exists;
    }

    @Test
    @DisplayName("Duplicate request → 409 Conflict (DuplicateException path)")
    void duplicateAsset() throws Exception {
        registersNewAsset();

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"mic":"XNYS","ticker":"AAPL","name":"Apple Inc."}
                    """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Invalid JSON (blank fields) → 400 Bad Request")
    void invalidPayload() throws Exception {
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"mic":"", "ticker":"   ", "name":""}
                    """))
                .andExpect(status().isBadRequest());
    }
}
