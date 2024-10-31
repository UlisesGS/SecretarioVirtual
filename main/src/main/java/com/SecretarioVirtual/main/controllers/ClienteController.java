package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.dtos.cliente.RequestClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteCreacionDto;
import com.SecretarioVirtual.main.services.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/registro-cliente")
    public ResponseEntity<ResponseClienteCreacionDto> createCliente(@RequestBody @Valid RequestClienteCreacionDto requestClienteCreacionDto){
        return ResponseEntity.ok(clienteService.createCliente(requestClienteCreacionDto));
    }
}
