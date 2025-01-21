package com.example.demo.repository;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    User findByUname(String uname); //通过用户名uname查找用户，注意要按照JPA的格式使用驼峰命名法
    User findByUnameAndPassword(String uname, String password);//通过用户名uname和密码查找用户
    User findByEmailAndPassword(String email, String password);
    User findByToken(String token);
    @Modifying
    @Query("UPDATE User u SET u.token = :token WHERE u.uid = :uid")
    void updateToken(@Param("uid") Long uid, @Param("token") String token);
}
