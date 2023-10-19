package com.authenticket.authenticket.dto.admin;

import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * A service class responsible for mapping instances of the {@link Admin} entity to
 * {@link AdminDisplayDto} DTOs and performing updates on admin user entities.
 */
@Service
public class AdminDtoMapper implements Function<Admin, AdminDisplayDto>  {
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs an instance of the AdminDtoMapper.
     *
     * @param passwordEncoder The password encoder used for encoding admin passwords.
     */
    @Autowired
    public AdminDtoMapper(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }

    /**
     * Maps an instance of the {@link Admin} entity to an {@link AdminDisplayDto}.
     *
     * @param admin The admin user entity to be mapped.
     * @return An AdminDisplayDto containing display information about the admin user.
     */
    public AdminDisplayDto apply(Admin admin) {
        return new AdminDisplayDto(
                admin.getAdminId(),
                admin.getName(),
                admin.getEmail(),
                Admin.getRole(),
                admin.getCreatedAt(),
                admin.getUpdatedAt(),
                admin.getDeletedAt()
        );
    }

    /**
     * Updates an existing admin user entity with the properties of a new admin user.
     * If the new admin user has a non-null name, it will update the name of the existing admin.
     * If the new admin user has a non-null password, it will encode and update the password of the existing admin.
     *
     * @param newAdmin The new admin user data containing updates.
     * @param oldAdmin The existing admin user to be updated.
     */
    public void update(Admin newAdmin, Admin oldAdmin){
        if(newAdmin.getName() != null){
            oldAdmin.setName(newAdmin.getName());
        }
        if(newAdmin.getPassword() != null){
            String passwordEncode = passwordEncoder.encode(newAdmin.getPassword());
            oldAdmin.setPassword(passwordEncode);
        }
    }
}
