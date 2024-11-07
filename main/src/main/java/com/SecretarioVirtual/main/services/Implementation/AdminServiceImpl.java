package com.SecretarioVirtual.main.services.Implementation;

import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFIndByApellidoDto;
import com.SecretarioVirtual.main.dtos.cliente.ResponseClienteFindAllDto;
import com.SecretarioVirtual.main.entity.Admin;
import com.SecretarioVirtual.main.entity.Cliente;
import com.SecretarioVirtual.main.entity.Disponibilidad;
import com.SecretarioVirtual.main.entity.Turno;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.mappers.AdminMapper;
import com.SecretarioVirtual.main.repositories.AdminRepository;
import com.SecretarioVirtual.main.repositories.ClienteRepository;
import com.SecretarioVirtual.main.repositories.DisponibilidadRepository;
import com.SecretarioVirtual.main.repositories.TurnoRepository;
import com.SecretarioVirtual.main.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ClienteRepository clienteRepository;

    private final DisponibilidadRepository disponibilidadRepository;

    private final TurnoRepository turnoRepository;

    private final AdminRepository adminRepository;

    private final AdminMapper adminMapper;

    @Override
    public List<ResponseClienteFindAllDto> findAllCliente() {
        List<Cliente> clientes = Optional.of(clienteRepository.findAll())
                .filter(lista -> !lista.isEmpty())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron clientes"));

        return adminMapper.clientesResponseFindAllDto(clientes);
    }

    @Override
    public ResponseClienteFIndByApellidoDto findByClienteApellido(String apellido) {
        Cliente cliente = clienteRepository.findByApellido(apellido).orElseThrow(() -> new ResourceNotFoundException("El apellido del cliente no existe"));

        return adminMapper.responseDtoClienteFindByApellido(cliente);

    }

    @Override
    public ResponseEntity<?> createDisponibilidad(List<Disponibilidad> disponibilidades) {

        disponibilidades.forEach(disponibilidadRepository::save);

        disponibilidades.forEach(dispo -> {

            if (dispo.getHoraInicio()!=null){
                generarTurnos(dispo.getHoraInicio(), dispo.getHoraFin(), dispo.getDiaSemana().toString());
            }
            if (dispo.getHoraInicioTarde()!=null){
                generarTurnos(dispo.getHoraInicioTarde(), dispo.getHoraFinTarde(), dispo.getDiaSemana().toString());
            }
            if (dispo.getHoraInicioNoche()!=null){
                generarTurnos(dispo.getHoraInicioNoche(), dispo.getHoraFinNoche(), dispo.getDiaSemana().toString());
            }

            disponibilidadRepository.save(dispo);
        });
        //Temporal
        Admin admin=new Admin();
        admin.setDisponibilidadLista(disponibilidades);
        adminRepository.save(admin);

        return ResponseEntity.ok("Agenda creada con exito.");
    }

    public void generarTurnos(LocalTime horaInicio, LocalTime horaFin, String dia) {

        LocalTime turnoInicio = horaInicio;

        while (turnoInicio.isBefore(horaFin)) {
            Turno turno = new Turno();

            LocalTime turnoFin = turnoInicio.plusHours(1);

            // Agrega el turno en formato de string, pero podrías también usar una clase para el turno si prefieres
            turno.setHora(turnoInicio);
            turno.setDia(dia);
            turnoRepository.save(turno);

            turnoInicio = turnoFin;
        }
    }
}
