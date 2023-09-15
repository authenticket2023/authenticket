package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    private final UserRepository userRepository;


    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public List<UserFullDisplayDto> findAllUser(){
        return userRepository.findAll()
                .stream()
                .map(userDtoMapper::fullApply)
                .collect(Collectors.toList());
    }

    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException{
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_MSG, email)));
    }

    public Optional<UserFullDisplayDto> findById(Integer userId) {
        return userRepository.findById(userId).map(userDtoMapper::fullApply);
    }

    public UserDisplayDto updateUser(User newUser){
        Optional<User> userOptional = userRepository.findByEmail(newUser.getEmail());

        if(userOptional.isPresent()){
            System.out.println("hello");
            User existingUser = userOptional.get();
            userDtoMapper.update(newUser, existingUser);
            userRepository.save(existingUser);
            return userDtoMapper.apply(existingUser);
        }

        return null;
    }

    public void deleteUser(Integer userId){
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if(user.getDeletedAt()!=null){
                throw new AlreadyDeletedException("User already deleted");
            }

            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new NonExistentException("User does not exists");
        }
    }

    public UserDisplayDto updateProfileImage(String filename, Integer userId){
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()){
            User existingUser = userOptional.get();
            existingUser.setProfileImage(filename);
            userRepository.save(existingUser);
            return userDtoMapper.apply(existingUser);
        }
        return null;
    }
}