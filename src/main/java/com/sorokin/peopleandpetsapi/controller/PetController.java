package com.sorokin.peopleandpetsapi.controller;

import com.sorokin.peopleandpetsapi.model.Pet;
import com.sorokin.peopleandpetsapi.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final Logger log = LoggerFactory.getLogger(PetController.class);

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Pet> createPet(@PathVariable("userId")
                                         @Positive
                                         @NotNull
                                         Long userID,
                                         @Valid
                                         @RequestBody Pet pet) {
        log.info("Create pet: {}", pet);
        Pet newPet = petService.createPet(userID, pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPet);
    }

    @GetMapping("/{id}")
    public Pet getPet(@PathVariable("id")
                      @NotNull
                      @Positive Long id) {
        log.info("Get pet: {}", id);
        Pet pet = petService.getPetById(id);
        return pet;
    }

    @GetMapping
    public List<Pet> getAllPets() {
        log.info("Get pets");
        return petService.getAllPets();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable("id")
                                         @NotNull
                                         @Positive Long id,
                                         @RequestBody
                                         @Valid Pet pet) {
        log.info("Update pet: {}", id);
        Pet updatedPet = petService.updatePet(id, pet);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable("id") Long id) {
        log.info("Delete pet: {}", id);
        petService.deletePetById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
