package Phone_Book;

import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 3, max = 20)
    @NotNull
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "ownerId")
    List<PhoneRecord> phoneBook;

    public User() {}

    public User(long id, @Size(min = 3, max = 20) String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<PhoneRecord> getPhoneBook() {
        return phoneBook;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((User)obj).getId();
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }
}
