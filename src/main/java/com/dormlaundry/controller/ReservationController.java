package com.dormlaundry.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import com.dormlaundry.model.Reservation;
import com.dormlaundry.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        Reservation saved = reservationService.createReservation(
                request.getMachineId(),
                username,
                request.getStartTime(),
                request.getEndTime()
        );

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        reservationService.deleteReservation(id, username);

        return ResponseEntity.noContent().build();
    }


    public static class CreateReservationRequest {
        @NotNull(message = "machineId is required")
        private Long machineId;

        @NotNull(message = "startTime is required")
        @Future(message = "startTime must be in the future")
        private LocalDateTime startTime;

        @NotNull(message = "endTime is required")
        private LocalDateTime endTime;

        public Long getMachineId() { return machineId; }
        public void setMachineId(Long machineId) { this.machineId = machineId; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    }

    // -----------------------------
    // COMPLETE RESERVATION
    // -----------------------------
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Reservation> completeReservation(@PathVariable Long id) {

        Reservation completed =
                reservationService.completeReservation(id);

        return ResponseEntity.ok(completed);
    }

    // -----------------------------
    // RESCHEDULE RESERVATION
    // -----------------------------
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<Reservation> rescheduleReservation(
            @PathVariable Long id,
            @RequestBody RescheduleRequest request) {

        Reservation updated =
                reservationService.rescheduleReservation(
                        id,
                        request.getNewStartTime(),
                        request.getNewEndTime()
                );

        return ResponseEntity.ok(updated);
    }

    // -----------------------------
    // DTO (Controller-level)
    // -----------------------------
    public static class RescheduleRequest {

        private LocalDateTime newStartTime;
        private LocalDateTime newEndTime;

        public LocalDateTime getNewStartTime() {
            return newStartTime;
        }

        public void setNewStartTime(LocalDateTime newStartTime) {
            this.newStartTime = newStartTime;
        }

        public LocalDateTime getNewEndTime() {
            return newEndTime;
        }

        public void setNewEndTime(LocalDateTime newEndTime) {
            this.newEndTime = newEndTime;
        }
    }
}