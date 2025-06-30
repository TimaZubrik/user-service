package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public void createUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getUsersById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }
    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
