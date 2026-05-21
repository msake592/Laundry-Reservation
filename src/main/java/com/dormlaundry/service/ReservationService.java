package com.dormlaundry.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dormlaundry.exception.ReservationNotAllowedException;
import com.dormlaundry.model.Machine;
import com.dormlaundry.model.MachineStatus;
import com.dormlaundry.model.Reservation;
import com.dormlaundry.model.ReservationStatus;
import com.dormlaundry.repository.MachineRepository;
import com.dormlaundry.repository.ReservationRepository;
import com.dormlaundry.repository.UserRepository;
import com.dormlaundry.model.User;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MachineRepository machineRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
            MachineRepository machineRepository,
            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation createReservation(Long machineId, String username, LocalDateTime start, LocalDateTime end) {

        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ReservationNotAllowedException("Machine not found"));
        System.out.println("CREATE RESERVATION CALLED");
        System.out.println("START = " + start);
        System.out.println("END = " + end);
        if (machine.getStatus() == MachineStatus.BROKEN) {
            throw new ReservationNotAllowedException("Machien is broken!");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ReservationNotAllowedException("It is a past time,enter a valid start");
        }
        if (!end.isAfter(start)) {
            throw new ReservationNotAllowedException("End time must be after start time");
        }

        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                machineId, start, end);

        if (!conflicts.isEmpty()) {
            throw new ReservationNotAllowedException("Time slot already reserved");
        }

        Reservation reservation = new Reservation();
        reservation.setMachine(machine);
        reservation.setUserId(username);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setStatus(ReservationStatus.ACTIVE);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation completeReservation(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotAllowedException("Reservation couldn't find!"));
        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByUsername(String username) {
        return reservationRepository.findByUserId(username);
    }

    @Transactional
    public void deleteReservation(Long id, String username) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotAllowedException("Reservation couldn't find!"));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ReservationNotAllowedException("User couldn't find!"));

        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isOwner = reservation.getUserId().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You can only delete your own reservations");
        }

        reservationRepository.delete(reservation);
    }

    @Transactional
    public Reservation rescheduleReservation(Long id, LocalDateTime newStart, LocalDateTime newEnd) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotAllowedException("Reservation couldn't find!"));

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new ReservationNotAllowedException("Completed reservation cannot be rescheduled");
        }

        Machine machine = reservation.getMachine();

        if (machine.getStatus() == MachineStatus.BROKEN) {
            throw new ReservationNotAllowedException("Machine is broken");
        }

        if (newStart.isBefore(LocalDateTime.now())) {
            throw new ReservationNotAllowedException(
                    "You must get a time machine for access this machine! It's a past time!");
        }

        if (!newEnd.isAfter(newStart)) {
            throw new ReservationNotAllowedException("Start and end times are not comptible!");
        }

        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                machine.getId(), newStart, newEnd);
        for (Reservation r : conflicts) {
            if (!r.getId().equals(reservation.getId())) {
                throw new ReservationNotAllowedException("Time slot already reserved");
            }
        }
        reservation.setStartTime(newStart);
        reservation.setEndTime(newEnd);

        return reservationRepository.save(reservation);
    }
}
