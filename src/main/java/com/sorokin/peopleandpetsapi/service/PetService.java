package com.sorokin.peopleandpetsapi.service;

import com.sorokin.peopleandpetsapi.exception.PetNotFoundException;
import com.sorokin.peopleandpetsapi.model.Pet;
import com.sorokin.peopleandpetsapi.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PetService {

    private Long idCounter;
    private final UserService userService;
    private final Map<Long, Pet> petMap;

    public PetService(UserService userService) {
        this.userService = userService;
        petMap = new HashMap<>();
        idCounter = 0L;
    }

    public Pet createPet(Long userID ,Pet pet) {
        User user = userService.getUserById(userID);
        Long newId = ++idCounter;
        pet.setId(newId);
        pet.setUserId(user.getId());
        user.addPet(pet);
        petMap.put(newId, pet);
        return pet;
    }

    public Pet getPetById(Long id) {
        return Optional.ofNullable(petMap.get(id)).orElseThrow(() -> new PetNotFoundException(String.format("Pet with id=%s not found", id)));
    }

    public List<Pet> getAllPets() {
        return new ArrayList<>(petMap.values());
    }

    public Pet updatePet(Long id, Pet pet) {
        Pet petToUpdate = getPetById(id);
        petToUpdate.setName(pet.getName());
        petToUpdate.setUserId(pet.getUserId());
        petMap.put(id, petToUpdate);
        return petToUpdate;
    }

    public void deletePetById(Long id) {
        Pet petToDelete = getPetById(id);
        User user=userService.getUserById(petToDelete.getUserId());
        user.removePet(petToDelete);
        petMap.remove(id);
    }
}
