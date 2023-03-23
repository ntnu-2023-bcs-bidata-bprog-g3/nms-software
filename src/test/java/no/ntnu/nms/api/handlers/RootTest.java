package no.ntnu.nms.api.handlers;

import no.ntnu.nms.logging.Logging;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(Root.class)
public class RootTest {

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    public static void setup() throws Exception {
        Logging.setUpLogger("ALL");
    }

    @Test
    public void TestRootGet() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("This endpoint " +
                        "is not in use. Please check the documentation for available endpoints."));
    }
}
