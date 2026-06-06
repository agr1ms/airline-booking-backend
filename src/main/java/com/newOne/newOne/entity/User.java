package com.newOne.newOne.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role = UserRole.USER;

	@Column(unique = true, nullable = true)
	private String aadharNumber;

	@Column(nullable = true)
	private String aadharFileName;

	@Column(nullable = false)
	private boolean aadharVerified = false;

	public User() {
	}

	public User(String username, String password, UserRole role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getAadharFileName() {
		return aadharFileName;
	}

	public void setAadharFileName(String aadharFileName) {
		this.aadharFileName = aadharFileName;
	}

	public boolean isAadharVerified() {
		return aadharVerified;
	}

	public void setAadharVerified(boolean aadharVerified) {
		this.aadharVerified = aadharVerified;
	}
}
