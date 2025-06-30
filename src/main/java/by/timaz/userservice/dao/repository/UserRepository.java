package by.timaz.userservice.dao.repository;

import by.timaz.userservice.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.Date;

public interface UserRepository extends JpaRepository<User, Long> {


    User findByBirthday(Date birthday);

    @Query(nativeQuery = true, value = """
        select * from users where email=:email
        """)
    User getUserByEmail(String email);


    @Modifying
    @Query(value = """
        delete from User u where u.id = :id
        """)
    User deleteUserById(Long id);


}
