package bluepumpkin.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bluepumpkin.domain.Account;
import bluepumpkin.repository.AccountRepository;

@Service
@Transactional(readOnly = true)
public class AccountService {
	
	private final AccountRepository accountRepository;
	
	@Autowired
	public AccountService(final AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		accountRepository.save(account);
		return account;
	}
	
	public Account findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

}
