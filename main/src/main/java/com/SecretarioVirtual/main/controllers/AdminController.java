package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFIndByApellidoDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFindAllDto;
import com.SecretarioVirtual.main.entity.Disponibilidad;
import com.SecretarioVirtual.main.services.AdminService;
import com.SecretarioVirtual.main.services.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/admin")
public class AdminController {


    private final AdminService adminService;

    @GetMapping("/lista-clientes")
    public ResponseEntity<List<ResponseClienteFindAllDto>> findAllCliente(){
        return ResponseEntity.ok(adminService.findAllCliente());
    }
    @GetMapping("/buscar-cliente/{apellido}")
    ResponseEntity<?> findByApellidoCliente(@PathVariable String apellido){
        return ResponseEntity.ok(adminService.findByClienteApellido(apellido));
    }

    @PostMapping("/crear-agenda")
    ResponseEntity<?> createListaDisponibilidad(@RequestBody @Valid List<Disponibilidad> disponibilidadesL){
        return ResponseEntity.ok(adminService.createDisponibilidad(disponibilidadesL));
    }
}
