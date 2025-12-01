package com.sorokin.peopleandpetsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorokin.peopleandpetsapi.model.User;
import com.sorokin.peopleandpetsapi.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserService userService;

    @Test
    void createUser() throws Exception {
        User user = new User(
                null,
                "Alex",
                "alex@gmail.com",
                40
        );
        String userJson = objectMapper.writeValueAsString(user);
        String createdUserJson = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();
        User createdUser = objectMapper.readValue(createdUserJson, User.class);
        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals(user.getEmail(), createdUser.getEmail());
    }

    @Test
    void createUserWithInvalidEmail() throws Exception {
        User user = new User(
                null,
                "Alex",
                "gmail.com",
                40
        );
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getUserWithRealId() throws Exception {
        User user = new User(
                null,
                "Alex",
                "gmail.com",
                40
        );
        userService.createUser(user);
        String foundUser = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        User foundedUser = objectMapper.readValue(foundUser, User.class);
        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(foundedUser);

    }

    @Test
    void getUserWithNotExistId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", Integer.MAX_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void deleteUser() throws Exception {
        User user = new User(
                null,
                "Alex",
                "gmail.com",
                40
        );
        userService.createUser(user);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void updateUser() throws Exception {
        User user = new User(
                null,
                "Alex",
                "alex@gmail.com",
                40
        );
        userService.createUser(user);
        user.setName("Bob");
        String updatedUser = objectMapper.writeValueAsString(user);
        String getUser = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUser))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        User userWithNewName = objectMapper.readValue(getUser, User.class);
        assertThat(userWithNewName.getName()).isEqualTo(user.getName());
    }
}