package com.SecretarioVirtual.main.services;


import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;

public interface UserService {

    ResponseUpdateUserDto updateUser(RequestUpdateUserDto updateUserDto);
    ResponseUpdateMailDto updateMail(RequestUpdateMailDto requestUpdateMailDto);
}
