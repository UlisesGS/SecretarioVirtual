package com.SecretarioVirtual.main.services;


import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestEmailUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestPasswordUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.ResponseUserDto;

import java.util.List;

public interface UserService {

    List<ResponseUserDto> getAllUsers();
    void deleteByEmail(RequestEmailUserDto requestEmailUserDto);
    ResponseUserDto getByEmail(RequestEmailUserDto requestEmailUserDto);
    void updatePassword(RequestPasswordUpdateUserDto requestPasswordUpdateUserDto);

    ResponseUpdateUserDto updateUser(RequestUpdateUserDto updateUserDto);
    ResponseUpdateMailDto updateMail(RequestUpdateMailDto requestUpdateMailDto);
}
