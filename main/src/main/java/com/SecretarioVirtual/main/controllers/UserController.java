package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestEmailUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestPasswordUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.ResponseUserDto;
import com.SecretarioVirtual.main.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("todos-usuarios")
    public ResponseEntity<List<ResponseUserDto>> findAll(){
        return ResponseEntity.ok().body(userService.getAllUsers());
    }


    @GetMapping("buscar-usuario")
    public ResponseEntity<ResponseUserDto> findByEmail(@Valid @RequestBody RequestEmailUserDto requestEmailUserDto){
        return ResponseEntity.ok().body(userService.getByEmail(requestEmailUserDto));
    }


    @PutMapping("editar-usuario")
    public ResponseEntity<ResponseUpdateUserDto> updateUser(@Valid @RequestBody RequestUpdateUserDto requestUpdateUserDto){
        ResponseUpdateUserDto responseUpdateUserDto = userService.updateUser(requestUpdateUserDto);
        return ResponseEntity.ok(responseUpdateUserDto);
    }


    @PutMapping("editar-contraseña")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody RequestPasswordUpdateUserDto requestPasswordUpdateUserDto){
        userService.updatePassword(requestPasswordUpdateUserDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @PutMapping("editar-mail")
    public ResponseEntity<ResponseUpdateMailDto> updateMail(@Valid @RequestBody RequestUpdateMailDto requestUpdateMailDto){
        ResponseUpdateMailDto responseUpdateMailDto = userService.updateMail(requestUpdateMailDto);
        return ResponseEntity.ok(responseUpdateMailDto);
    }


    @DeleteMapping("eliminar-usuario")
    public ResponseEntity<?> deleteByEmail(@Valid @RequestBody RequestEmailUserDto requestEmailUserDto){
        userService.deleteByEmail(requestEmailUserDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
