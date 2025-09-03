package ru.john;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.john.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private UserDto createTestUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Петя");
        dto.setEmail("petr@mail.com");
        dto.setAge(25);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readValue(response, UserDto.class);
    }

    @Test
    void createUser() throws Exception {
        UserDto dto = createTestUser();
        assertNotNull(dto.getId());
    }

    @Test
    void getAllUsers() throws Exception {
        createTestUser();

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void getUserById() throws Exception {
        UserDto user = createTestUser();

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void updateUser() throws Exception {
        UserDto user = createTestUser();
        user.setName("Вася");
        user.setEmail("vasya@mail.com");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Вася"))
                .andExpect(jsonPath("$.email").value("vasya@mail.com"));
    }

    @Test
    void deleteUser() throws Exception {
        UserDto user = createTestUser();

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isNotFound());
    }
}