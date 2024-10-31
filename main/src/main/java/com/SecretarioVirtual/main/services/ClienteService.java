package com.SecretarioVirtual.main.services;

import com.SecretarioVirtual.main.dtos.cliente.RequestClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFindAllDto;

import java.util.List;

public interface ClienteService {

    ResponseClienteCreacionDto createCliente(RequestClienteCreacionDto requestClienteCreacionDto);
    List<ResponseClienteFindAllDto>findAllCliente();
}
