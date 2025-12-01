package com.sorokin.peopleandpetsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorokin.peopleandpetsapi.model.Pet;
import com.sorokin.peopleandpetsapi.model.User;
import com.sorokin.peopleandpetsapi.service.PetService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureMockMvc
@SpringBootTest
class PetControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PetService petService;
    @Autowired
    private UserService userService;

    @Test
    void createPet() throws Exception {
        Pet pet = new Pet(
                null,
                "Cat"
        );
        User user = new User(
                null,
                "Alex",
                "alex@gmail.com",
                40
        );
        userService.createUser(user);
        String petJson = objectMapper.writeValueAsString(pet);
        String createdPetJson = mockMvc.perform(MockMvcRequestBuilders.post("/{userId}/pets", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Pet createdPet = objectMapper.readValue(createdPetJson, Pet.class);
        Assertions.assertNotNull(createdPet.getId());
        Assertions.assertEquals(createdPet.getName(), pet.getName());
    }

    @Test
    void createPetWithInvalidName() throws Exception {
        Pet pet = new Pet(
                null,
                null
        );
        User user = new User(
                null,
                "Alex",
                "alex@gmail.com",
                40
        );
        userService.createUser(user);
        String petJson = objectMapper.writeValueAsString(pet);
        mockMvc.perform(MockMvcRequestBuilders.post("/{userId}/pets", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getPetWithRealId() throws Exception {
        Pet pet = new Pet(
                null,
                "Cat"
        );
        User user = new User(
                null,
                "Alex",
                "gmail.com",
                40
        );
        userService.createUser(user);
        petService.createPet(user.getId(), pet);
        String foundPet = mockMvc.perform(MockMvcRequestBuilders.get("/pets/{id}", pet.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Pet foundedPet = objectMapper.readValue(foundPet, Pet.class);
        assertThat(pet)
                .usingRecursiveComparison()
                .isEqualTo(foundedPet);
    }

    @Test
    void getPetWithNotExistIdD() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/pets/{id}", Integer.MAX_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updatePet() throws Exception {
        Pet pet = new Pet(
                null,
                "Cat"
        );
        User user = new User(
                null,
                "Alex",
                "gmail.com",
                40
        );
        userService.createUser(user);
        petService.createPet(user.getId(), pet);
        pet.setName("Dog");
        String petJson = objectMapper.writeValueAsString(pet);
        String getPet = mockMvc.perform(MockMvcRequestBuilders.put("/pets/{id}", pet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Pet updatedPet = objectMapper.readValue(getPet, Pet.class);
        assertThat(updatedPet.getName().equals(pet.getName()));
    }

    @Test
    void deletePet() throws Exception {
        Pet pet = new Pet(
                null,
                "Cat"
        );
        User user = new User(
                null,
                "Alex",
                "gmail.com",
                40
        );
        userService.createUser(user);
        petService.createPet(user.getId(), pet);
        mockMvc.perform(MockMvcRequestBuilders.delete("/pets/{id}", pet.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        mockMvc.perform(MockMvcRequestBuilders.get("/pets/{id}", pet.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}