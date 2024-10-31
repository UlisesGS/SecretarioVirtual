package com.SecretarioVirtual.main.services.Implementation;

import com.SecretarioVirtual.main.dtos.cliente.RequestClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteCreacionDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFindAllDto;
import com.SecretarioVirtual.main.entity.Cliente;
import com.SecretarioVirtual.main.mappers.ClienteMapper;
import com.SecretarioVirtual.main.repositories.ClienteRepository;
import com.SecretarioVirtual.main.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    private final ClienteMapper clienteMapper;


    @Override
    public ResponseClienteCreacionDto createCliente(RequestClienteCreacionDto requestClienteCreacionDto) {

        Cliente cliente = clienteMapper.requestDtoClienteCreation(requestClienteCreacionDto);
        clienteRepository.save(cliente);//mandar email de confirmacion o en la aplicacion
        return clienteMapper.clienteToResponseDto(cliente,"Se envio la peticion del registro con exito");
    }

    @Override
    public List<ResponseClienteFindAllDto> findAllCliente() {
        List<Cliente>clientes = clienteRepository.findAll();
        return clienteMapper.clientesResponseFindAllDto(clientes);
    }
}
