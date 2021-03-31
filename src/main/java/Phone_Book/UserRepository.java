package Phone_Book;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select u from User u where lower(u.name) like concat('%', lower(:name),'%')")
    List<User> findByName(@Param("name") String name);
}
