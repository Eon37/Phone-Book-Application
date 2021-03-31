package Phone_Book;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    UserService userService;

    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "getAllUsers", notes = "Get list of users", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    })
    @GetMapping(path = "/api/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ApiOperation(value = "createUser", notes = "Create user. Specify name with length [3; 20]", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation failed for object='user'")
    })
    @PostMapping(path = "/api/users")
    public User createUser(@Valid @RequestBody User user){
        userService.saveUser(user);
        return user;
    }

    @ApiOperation(value = "getUserById", notes = "Create user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "User not found with ID={id}")
    })
    @GetMapping(path = "/api/users/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getById(id);
    }

    @ApiOperation(value = "deleteUser", notes = "Delete user with id = {id}")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "OK"),
            @ApiResponse(code = 404, message = "User not found with ID={id}")
    })
    @DeleteMapping(path = "/api/users/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        userService.delete(userService.getById(id));
    }

    @ApiOperation(value = "updateUser", notes = "Update user with id = {id}")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 404, message = "User not found with ID={id}")
    })
    @PutMapping(path = "/api/users/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable long id, @Valid @RequestBody User updatedUser) {
        userService.updateUser(id, updatedUser);
    }

    @ApiOperation(value = "getByName", notes = "Get user by name", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    })
    @GetMapping(path = "/api/users/search")
    public List<User> getByName(@RequestParam String name) {
        return userService.getUsersByName(name);
    }

}
