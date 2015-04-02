package bluepumpkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bluepumpkin.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

		Account findByEmail(String email);
}
