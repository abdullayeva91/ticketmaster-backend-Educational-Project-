package com.ticketmaster.ticketmastereventservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmastereventservice.dto.request.CreateEventRequest;
import com.ticketmaster.ticketmastereventservice.enums.EventStatus;
import com.ticketmaster.ticketmastereventservice.model.Category;
import com.ticketmaster.ticketmastereventservice.model.Venue;
import com.ticketmaster.ticketmastereventservice.repository.CategoryRepository;
import com.ticketmaster.ticketmastereventservice.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @WithMockUser(roles = "ADMIN") // ← security üçün
    void shouldCreateEvent() throws Exception {
        Venue venue = venueRepository.save(Venue.builder()
                .name("Baku Crystal Hall")
                .capacity(200)
                .city("Bakı")
                .address("Neftçilər pr.")
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("Konsert")
                .description("Musiqi tədbirləri")
                .build());

        CreateEventRequest request = new CreateEventRequest();
        request.setName("Konsert");
        request.setVenueId(venue.getId());
        request.setCategoryId(category.getId()); // ← əlavə edildi
        request.setTotalCapacity(100);
        request.setPrice(BigDecimal.valueOf(50));
        request.setEventDate(LocalDateTime.now().plusDays(5));
        request.setEventStatus(EventStatus.PUBLISHED);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Konsert"));
    }
}