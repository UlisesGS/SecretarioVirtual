package com.SecretarioVirtual.main.mappers;

import com.SecretarioVirtual.main.dtos.cliente.RequestClienteFindByApellidoDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFIndByApellidoDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFindAllDto;
import com.SecretarioVirtual.main.entity.Cliente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    List<ResponseClienteFindAllDto> clientesResponseFindAllDto(List<Cliente>clientes);

    Cliente requesDtoClienteCreation(RequestClienteFindByApellidoDto requestClienteFindByApellidoDto);

    ResponseClienteFIndByApellidoDto responseDtoClienteFindByApellido(Cliente cliente);
}
