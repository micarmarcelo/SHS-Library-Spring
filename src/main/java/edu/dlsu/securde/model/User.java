package edu.dlsu.securde.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users_list")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "FirstName")
	private String firstName;
	@Column(name = "MiddleInitial")
	private String middleInitial;
	@Column(name = "LastName")
	private String lastName;
	@Column(name = "Username")
	private String username;
	@Column(name = "Password")
	private String password;
	@Column(name = "Email")
	private String email;
	@Column(name = "StudentEmployeeNumber")
	private double studentEmployeeNumber;
	@Column(name = "Birthday")
	private String birthday;
	@Column(name = "SecretQuestion")
	private String secretQuestion;
	@Column(name = "SecretAnswer")
	private String secretAnswer;
	@Column(name = "UserType")
	private Integer userType;
	@Column(name = "Locked")
	private Integer locked;
	@Column(name = "WrongAttempts")
	private Integer wrongAttempts;
	// 0-admin,staff,manager,1-faculty,2-student..
	@Column(name = "AccountType")
	private Integer accountType;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "ID"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

	public User() {

	}

	public User(Integer id, String firstName, String middleInitial, String lastName, String username, String password,
			String email, double studentEmployeeNumber, String birthday, String secretQuestion, String secretAnswer,
			Integer userType, Integer locked, Integer wrongAttempts, Integer accountType, Set<Role> roles) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.middleInitial = middleInitial;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.email = email;
		this.studentEmployeeNumber = studentEmployeeNumber;
		this.birthday = birthday;
		this.secretQuestion = secretQuestion;
		this.secretAnswer = secretAnswer;
		this.userType = userType;
		this.locked = locked;
		this.wrongAttempts = wrongAttempts;
		this.accountType = accountType;
		this.roles = roles;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public double getStudentEmployeeNumber() {
		return studentEmployeeNumber;
	}

	public void setStudentEmployeeNumber(double studentEmployeeNumber) {
		this.studentEmployeeNumber = studentEmployeeNumber;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSecretQuestion() {
		return secretQuestion;
	}

	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}

	public String getSecretAnswer() {
		return secretAnswer;
	}

	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getLocked() {
		return locked;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public Integer getWrongAttempts() {
		return wrongAttempts;
	}

	public void setWrongAttempts(Integer wrongAttempts) {
		this.wrongAttempts = wrongAttempts;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}