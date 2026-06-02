package com.newOne.newOne.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "boarding_passes")
public class BoardingPass {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "booking_id", nullable = false, unique = true)
	private Booking booking;

	@Column(nullable = false, unique = true)
	private String boardingPassNumber;

	@Column(nullable = false)
	private String assignedSeats;

	@Column(nullable = false)
	private String gate;

	@Column(nullable = false)
	private LocalDateTime boardingTime;

	@Column(nullable = false)
	private LocalDateTime checkedInAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public String getBoardingPassNumber() {
		return boardingPassNumber;
	}

	public void setBoardingPassNumber(String boardingPassNumber) {
		this.boardingPassNumber = boardingPassNumber;
	}

	public String getAssignedSeats() {
		return assignedSeats;
	}

	public void setAssignedSeats(String assignedSeats) {
		this.assignedSeats = assignedSeats;
	}

	public String getGate() {
		return gate;
	}

	public void setGate(String gate) {
		this.gate = gate;
	}

	public LocalDateTime getBoardingTime() {
		return boardingTime;
	}

	public void setBoardingTime(LocalDateTime boardingTime) {
		this.boardingTime = boardingTime;
	}

	public LocalDateTime getCheckedInAt() {
		return checkedInAt;
	}

	public void setCheckedInAt(LocalDateTime checkedInAt) {
		this.checkedInAt = checkedInAt;
	}
}
