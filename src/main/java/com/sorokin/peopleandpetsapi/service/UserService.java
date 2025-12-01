package com.sorokin.peopleandpetsapi.service;

import com.sorokin.peopleandpetsapi.exception.UserNotFoundException;
import com.sorokin.peopleandpetsapi.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {


    private Long idCounter;
    private final Map<Long, User> userMap;

    public UserService() {
        idCounter = 0L;
        userMap = new HashMap<>();
    }

    public User createUser(User user) {
        Long newId = ++idCounter;
        user.setId(newId);
        userMap.put(newId, user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User getUserById(Long id) {
        return validateUser(id);
    }

    public void deleteUserById(Long id) {
        Optional.ofNullable(userMap.remove(id))
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%s not found", id)));
    }

    public User updateUser(Long id, User user) {
        User userToUpdate = validateUser(id);
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setAge(user.getAge());
        userToUpdate.setPets(user.getPets());
        userMap.put(id, userToUpdate);
        return userToUpdate;
    }

    private User validateUser(Long id) {
        return Optional.ofNullable(userMap.get(id))
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%s not found", id)));
    }
}
