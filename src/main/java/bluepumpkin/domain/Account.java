package bluepumpkin.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.ElementCollection;
//import javax.persistence.CollectionTable;
//import javax.persistence.JoinColumn;
import javax.persistence.Column;
//import javax.persistence.JoinTable;
//import javax.persistence.MapKeyColumn;
//import javax.persistence.OrderBy;
//import javax.persistence.OrderColumn;
//import javax.persistence.Lob;
//import javax.persistence.Basic;
//import javax.persistence.Table;
//import javax.persistence.OneToOne;




import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;



//import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@SuppressWarnings("serial")
@Entity
public class Account implements Serializable {

	public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;
	
	@JsonIgnore
	private String password;
	
	private String role = ROLE_USER;
	
//	@OneToOne(mappedBy = "account")
//	private Employee employee;
	
	protected Account() {
	}
	
//	for testing
	public Account(String email) {
		this.email = email;
	}
	
	public Account(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public Account(String email, String password, String role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String param) {
		this.email = param;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String param) {
		this.password = param;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String param) {
		this.role = param;
	}

//	public Employee getEmployee() {
//	    return employee;
//	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(getRole()));
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(getRole());
    }
}