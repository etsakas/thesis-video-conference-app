package app.repo;

import java.util.List;
import app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String>{
	User findByUserName(String userName);
	User findByUserEmail(String userEmail);
	List<User> findAll();
}