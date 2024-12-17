package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;
import com.SecretarioVirtual.main.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("editar-usuario")
    public ResponseEntity<ResponseUpdateUserDto> updateUser(@Valid @RequestBody RequestUpdateUserDto requestUpdateUserDto){
        ResponseUpdateUserDto responseUpdateUserDto = userService.updateUser(requestUpdateUserDto);
        return ResponseEntity.ok(responseUpdateUserDto);
    }


    @PostMapping("editar-mail")
    public ResponseEntity<ResponseUpdateMailDto> updateMail(@Valid @RequestBody RequestUpdateMailDto requestUpdateMailDto){
        ResponseUpdateMailDto responseUpdateMailDto = userService.updateMail(requestUpdateMailDto);
        return ResponseEntity.ok(responseUpdateMailDto);
    }
}
