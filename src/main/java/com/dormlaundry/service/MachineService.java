package com.dormlaundry.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dormlaundry.exception.ResourceNotFoundException;
import com.dormlaundry.model.Machine;
import com.dormlaundry.model.MachineStatus;
import com.dormlaundry.model.Reservation;
import com.dormlaundry.model.ReservationStatus;
import com.dormlaundry.repository.MachineRepository;
import com.dormlaundry.repository.ReservationRepository;

@Service
public class MachineService {

    private final MachineRepository machineRepository;
    private final ReservationRepository reservationRepository;

    // ctor injection
    public MachineService(MachineRepository machineRepository,
                          ReservationRepository reservationRepository) {
        this.machineRepository = machineRepository;
        this.reservationRepository = reservationRepository;
    }

    public Machine addMachine(Machine machine) {
        return machineRepository.save(machine);
    }

    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }

    public Optional<Machine> getMachineById(Long id) {
        return machineRepository.findById(id);
    }

    @Transactional
    public Optional<Machine> markMachineAsBroken(Long id) {
        Optional<Machine> machineOpt = machineRepository.findById(id);

        if (machineOpt.isPresent()) {
            Machine machine = machineOpt.get();
            machine.setStatus(MachineStatus.BROKEN);

            // cancel the future reservations
            List<Reservation> reservations =
                reservationRepository.findByMachineIdAndStatusAndStartTimeAfter(
                    machine.getId(),
                    ReservationStatus.ACTIVE,
                    LocalDateTime.now()
                );

            for (Reservation r : reservations) {
                r.setStatus(ReservationStatus.CANCELLED);
            }

            machineRepository.save(machine);
        }

        return machineOpt;
    }

    public boolean deleteMachine(Long id) {
        if (!machineRepository.existsById(id)) {
            return false;
        }
        machineRepository.deleteById(id);
        return true;
    }
    public Machine updateMachineStatus(Long machineId, MachineStatus status) {
        Machine machine = machineRepository.findById(machineId)
               .orElseThrow(() -> new ResourceNotFoundException("Machine not found with id: " + machineId));
    
        machine.setStatus(status);
    
        if (status == MachineStatus.BROKEN) {
            List<Reservation> futureReservations =
                    reservationRepository.findByMachineIdAndStatusAndStartTimeAfter(
                            machineId,
                            ReservationStatus.ACTIVE,
                            LocalDateTime.now()
                    );
    
            for (Reservation reservation : futureReservations) {
                reservation.setStatus(ReservationStatus.CANCELLED);
            }
    
            reservationRepository.saveAll(futureReservations);
        }
    
        return machineRepository.save(machine);
    }
}