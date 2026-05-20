package com.dormlaundry.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dormlaundry.model.Machine;
import com.dormlaundry.service.MachineService;

@RestController
@RequestMapping("/machines")
public class MachineController {
    private final MachineService machineService;
    public MachineController(MachineService machineService){
        this.machineService=machineService;
    }

    @PostMapping
    public Machine addMachine(@RequestBody Machine machine){
        System.out.println("MACHINE CONTROLLER: addMachine reached with " + machine);
        return machineService.addMachine(machine);
    }

    @GetMapping
    public List<Machine> getAllMachines(){
        return machineService.getAllMachines();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Machine> getMachine(@PathVariable Long id){
        Optional<Machine> machineOptional = machineService.getMachineById(id);
        return machineOptional.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/broken")
    public ResponseEntity<Machine> markAsBroken(@PathVariable Long id){
        Optional<Machine> machineOptional = machineService.markMachineAsBroken(id);
        return machineOptional.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }
    @PatchMapping("/{id}/available")
    public ResponseEntity<Machine> markAsAvailable(@PathVariable Long id){
        Optional<Machine> machineOptional = machineService.markMachineAsAvailable(id);
        return machineOptional.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable Long id){
        boolean deleted = machineService.deleteMachine(id);
        if(!deleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
