package com.SecretarioVirtual.main.mappers;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.dtos.security.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User registerUserToUser(RequestRegisterDto requestRegisterDto);

    ResponseUserNonVerifiedDto userToUserNonVerifiedDto(User user);

    ResponseUserVerifiedDto userToUserVerifiedDto(User user);

    User updateUserDtoToUser(RequestUpdateUserDto updateUserDto);

    ResponseUpdateUserDto userToResponseUpdateUserDto(User user);


    ResponseUpdateMailDto userToResponseUpdateMailDto(User user);
}

