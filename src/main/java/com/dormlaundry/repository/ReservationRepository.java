package com.dormlaundry.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dormlaundry.model.Reservation;
import com.dormlaundry.model.ReservationStatus;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
            SELECT r FROM Reservation r
            WHERE r.machine.id = :machineId
            AND  r.status='ACTIVE'
            AND (
                (:start<r.endTime) AND (:end>r.startTime)
            )
            """)
    List<Reservation> findConflictingReservations(
        @Param("machineId") Long machineId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByMachineIdAndStatusAndStartTimeAfter(
        Long machineId,
        ReservationStatus status,
        LocalDateTime time
    );
    
}