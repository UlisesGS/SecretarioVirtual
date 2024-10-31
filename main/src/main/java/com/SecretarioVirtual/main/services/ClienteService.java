package com.SecretarioVirtual.main.services;

import com.SecretarioVirtual.main.dtos.cliente.RequestClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteCreacionDto;

public interface ClienteService {

    ResponseClienteCreacionDto createCliente(RequestClienteCreacionDto requestClienteCreacionDto);
}
