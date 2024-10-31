package com.SecretarioVirtual.main.mappers;


import com.SecretarioVirtual.main.dtos.cliente.RequestClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteCreacionDto;
import com.SecretarioVirtual.main.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    //CREATE
    Cliente requestDtoClienteCreation(RequestClienteCreacionDto requestClienteCreacionDto);

    @Mapping(source = "mensaje1", target = "mensaje")
    ResponseClienteCreacionDto clienteToResponseDto(Cliente cliente, String mensaje1);
}
