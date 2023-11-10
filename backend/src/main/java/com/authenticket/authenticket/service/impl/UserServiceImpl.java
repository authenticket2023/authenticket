package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface that provides user-related operations.
 */

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

    /**
     * Retrieves a list of all users and maps them to UserFullDisplayDto objects.
     *
     * @return A list of UserFullDisplayDto objects representing all users.
     */
    public List<UserFullDisplayDto> findAllUser(){
        return userRepository.findAll()
                .stream()
                .map(userDtoMapper::fullApply)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException{
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_MSG, email)));
    }

    /**
     * Finds a user by their unique identifier and maps it to a UserFullDisplayDto.
     *
     * @param userId The unique identifier of the user.
     * @return An optional containing a UserFullDisplayDto, or an empty optional if the user is not found.
     */
    @Override
    public Optional<UserFullDisplayDto> findById(Integer userId) {
        return userRepository.findById(userId).map(userDtoMapper::fullApply);
    }

    /**
     * Finds a user by their unique identifier.
     *
     * @param userId The unique identifier of the user.
     * @return An optional containing the User object, or an empty optional if the user is not found.
     */
    @Override
    public Optional<User> findUserById(Integer userId) {
        return userRepository.findUserByUserId(userId);
    }
//    public Optional<User> findById(Integer userId){
//        return userRepository.findById(userId);
//    }

    /**
     * Updates the information of a user.
     *
     * @param newUser The updated user information.
     * @return A UserDisplayDto representing the updated user, or null if the user is not found.
     */
    @Override
    public UserDisplayDto updateUser(User newUser){
        Optional<User> userOptional = userRepository.findByEmail(newUser.getEmail());

        if(userOptional.isPresent()){
            User existingUser = userOptional.get();
            userDtoMapper.update(newUser, existingUser);
            userRepository.save(existingUser);
            return userDtoMapper.apply(existingUser);
        }

        return null;
    }

    /**
     * Deletes a user by marking them as deleted with a timestamp.
     *
     * @param userId The unique identifier of the user to be deleted.
     * @throws AlreadyDeletedException If the user is already deleted.
     * @throws NonExistentException If the user does not exist.
     */
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

    /**
     * Updates the profile image filename for a user.
     *
     * @param filename The new profile image filename.
     * @param userId The unique identifier of the user.
     * @return A UserDisplayDto with the updated profile image information, or null if the user is not found.
     */
    @Override
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