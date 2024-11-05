package com.SecretarioVirtual.main.services;

import com.SecretarioVirtual.main.dtos.cliente.*;

import java.util.List;

public interface ClienteService {

    ResponseClienteCreacionDto createCliente(RequestClienteCreacionDto requestClienteCreacionDto);
    List<ResponseClienteFindAllDto>findAllCliente();
    ResponseClienteFIndByApellidoDto findByClienteApellido(String apellido);
}
