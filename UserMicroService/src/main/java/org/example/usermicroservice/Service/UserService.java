package org.example.usermicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.usermicroservice.Model.User;
import org.example.usermicroservice.Repository.UserRepository;
import org.example.usermicroservice.UserDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    private UserDTO userToUserDTOMapper(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole().getRole());
    }

    @Cacheable(value = "userId", key = "#id")
    public UserDTO findById(int id) {
        User user = new User();
        return userToUserDTOMapper(userRepository.findById(id).orElse(user));
    }

    @Cacheable(value = "userUsername", key = "#username", unless = "#result == null")
    public UserDTO findByUsername(String username) {
        User user = new User();
        return userToUserDTOMapper(userRepository.findUserByUsername(username).orElse(user));
    }
}
