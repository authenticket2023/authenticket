package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.controller.AuthResponse.AuthenticationAdminResponse;
import com.authenticket.authenticket.controller.AuthResponse.AuthenticationOrgResponse;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.controller.AuthResponse.AuthenticationUserResponse;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.AwaitingVerificationException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.AuthenticationService;
import com.authenticket.authenticket.service.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl extends Utility implements AuthenticationService {

    @Value("${authenticket.api-port}")
    private String apiPort;

    // User repos
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EventOrganiserRepository organiserRepository;

    //JwtService
    @Autowired
    private JwtServiceImpl jwtServiceImpl;

    //Authentication
    @Autowired
    private AuthenticationManager authenticationManager;

    //Email Sender
    @Autowired
   private EmailServiceImpl emailServiceImpl;

    //UserDTO
    @Autowired
    private UserDtoMapper userDTOMapper;

    //EventOrgDTO
    @Autowired
    private EventOrganiserDtoMapper eventOrgDtoMapper;

    //AdminDTO
    @Autowired
    private AdminDtoMapper adminDtoMapper;

    //user
    public void userRegister(User request) {


        var existingUser = userRepository.findByEmail(request.getEmail());

        if(existingUser.isPresent()){
            if(!existingUser.get().getEnabled()){
                throw new AlreadyExistsException("Verification needed");
            }
            throw new AlreadyExistsException("User already exists");
        }

        userRepository.save(request);
        var jwtToken = jwtServiceImpl.generateToken(request);

        String link = "http://localhost:" + apiPort + "/api/auth/register/confirm?token=" + jwtToken;
        emailServiceImpl.send(request.getEmail(), buildEmail(request.getName(), link));
    }

    public AuthenticationUserResponse userAuthenticate(String email, String password){
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        var jwtToken = jwtServiceImpl.generateToken(user);

        return AuthenticationUserResponse.builder()
                .token(jwtToken)
                .userDetails(userDTOMapper.apply(user))
                .build();
    }

    public AuthenticationUserResponse confirmUserToken(String token) {
        if (jwtServiceImpl.isTokenExpired(token)) {
                throw new AwaitingVerificationException("Token expired");
        }

        String email = jwtServiceImpl.extractUsername(token);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        if (user.getEnabled()){
            throw new IllegalStateException("Email already confirmed");
        }

        userRepository.enableAppUser(email);

        var jwtToken = jwtServiceImpl.generateToken(user);
        return AuthenticationUserResponse.builder()
                .token(jwtToken)
                .userDetails(userDTOMapper.apply(user))
                .build();
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    public void orgRegister (EventOrganiser request){

        var existingOrg = organiserRepository.findByEmail(request.getEmail());

        if(existingOrg.isPresent()){
            if(!existingOrg.get().getEnabled()){
                throw new AlreadyExistsException("Awaiting approval");
            }
            throw new AlreadyExistsException("User already exists");
        }

        organiserRepository.save(request);
    }

    public AuthenticationOrgResponse orgAuthenticate(String email, String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        var eventOrg = organiserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Event Organiser does not exist"));
        var jwtToken = jwtServiceImpl.generateToken(eventOrg);
        System.out.println(jwtToken);
        return AuthenticationOrgResponse.builder()
                .token(jwtToken)
                .orgDetails(eventOrgDtoMapper.apply(eventOrg))
                .build();
    }

    public void adminRegister (Admin request){

        var existingAdmin = adminRepository.findByEmail(request.getEmail());

        if(existingAdmin.isPresent()){
            throw new AlreadyExistsException("Admin already exists");
        }
        adminRepository.save(request);
    }

    public AuthenticationAdminResponse adminAuthenticate(String email, String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        var admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin does not exist"));
        var jwtToken = jwtServiceImpl.generateToken(admin);
        System.out.println(jwtToken);
        return AuthenticationAdminResponse.builder()
                .token(jwtToken)
                .adminDetails(adminDtoMapper.apply(admin))
                .build();
    }
}
