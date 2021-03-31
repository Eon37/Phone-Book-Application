package Phone_Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;

    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID=" + id));
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void updateUser(long id, User updatedUser) {
        User user = getById(id);

        user.setName(updatedUser.getName());
        saveUser(user);
    }

    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }

    public List<PhoneRecord> getBookByOwner(long id) {
        return getById(id).getPhoneBook();
    }
}
