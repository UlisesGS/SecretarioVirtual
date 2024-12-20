package com.SecretarioVirtual.main.mappers;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.dtos.security.*;
import com.SecretarioVirtual.main.entities.dtos.security.User.ResponseUserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<ResponseUserDto> userListToResponseUserDto(List<User> userList);

    ResponseUserDto userToResponseUserDto(User user);

    // SECURITY
    User registerUserToUser(RequestRegisterDto requestRegisterDto);

    ResponseUserNonVerifiedDto userToUserNonVerifiedDto(User user);

    ResponseUserVerifiedDto userToUserVerifiedDto(User user);

    User updateUserDtoToUser(RequestUpdateUserDto updateUserDto);

    ResponseUpdateUserDto userToResponseUpdateUserDto(User user);

    ResponseUpdateMailDto userToResponseUpdateMailDto(User user);
}

