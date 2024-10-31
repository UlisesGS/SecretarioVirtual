package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.dtos.cliente.RequestClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFindAllDto;
import com.SecretarioVirtual.main.services.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    @GetMapping("/findAll-clientes")
    public ResponseEntity<List<ResponseClienteFindAllDto>> findAllCliente(){
        return ResponseEntity.ok(clienteService.findAllCliente());
    }
    @PostMapping("/registro-cliente")
    public ResponseEntity<ResponseClienteCreacionDto> createCliente(@RequestBody @Valid RequestClienteCreacionDto requestClienteCreacionDto){
        return ResponseEntity.ok(clienteService.createCliente(requestClienteCreacionDto));
    }
}
