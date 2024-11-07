package com.SecretarioVirtual.main.services;

import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFIndByApellidoDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFindAllDto;
import com.SecretarioVirtual.main.entity.Disponibilidad;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {

    List<ResponseClienteFindAllDto> findAllCliente();
    ResponseClienteFIndByApellidoDto findByClienteApellido(String apellido);

    ResponseEntity<?> createDisponibilidad(List<Disponibilidad> disponibilidades);
}
