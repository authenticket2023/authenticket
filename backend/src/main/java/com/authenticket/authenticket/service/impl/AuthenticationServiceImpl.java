package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.controller.authentication.AuthenticationResponse;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${authenticket.api-port}")
    private String apiPort;

    @Autowired
    private UserRepository repository;

    //Registration
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public ResponseEntity<AuthenticationResponse> register(User request) {
        AuthenticationResponse badReq;
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .enabled(false)
                //role of user to take note of
                .build();

        var existing = repository.findByEmail(request.getEmail())
                .isPresent();
        if(existing){
//            throw new IllegalStateException("User already exists");
            badReq = AuthenticationResponse
                    .builder()
                    .message("User already exists")
                    .build();

            return ResponseEntity.status(400).body(badReq);
        }

        repository.save(user);
        var jwtToken = jwtServiceImpl.generateToken(user);

        String link = "http://localhost:" + apiPort + "/api/auth/register/confirm?token=" + jwtToken;
        emailServiceImpl.send(request.getEmail(), buildEmail(request.getName(), link));

        AuthenticationResponse goodReq = AuthenticationResponse.builder()
                .message("Verification required")
                .build();

        return ResponseEntity.status(200).body(goodReq);
    }

    public ResponseEntity<AuthenticationResponse> authenticate(User request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
//                .orElse(null);
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        //no exception here
        var jwtToken = jwtServiceImpl.generateToken(user);

        AuthenticationResponse goodReq =  AuthenticationResponse.builder()
                .message("welcome1 " + user.getName())
                .token(jwtToken)
                .userDetails(userDTOMapper.apply(user))
                .build();
        return ResponseEntity.status(200).body(goodReq);
    }

    public ResponseEntity<AuthenticationResponse> confirmToken(String token) {
        AuthenticationResponse badReq;
        if (jwtServiceImpl.isTokenExpired(token)) {
//           throw new IllegalStateException("token expired");
                badReq = AuthenticationResponse
                        .builder()
                        .message("Token expired")
                        .build();

                return ResponseEntity.status(403).body(badReq);
        }

        String email = jwtServiceImpl.extractUsername(token);

        var user = repository.findByEmail(email)
//                .orElse(null);
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        if (user.getEnabled()){
//            throw new IllegalStateException("email already confirmed");
            badReq = AuthenticationResponse
                    .builder()
                    .message("Email already confirmed")
                    .build();

            return ResponseEntity.status(400).body(badReq);
        }

        repository.enableAppUser(email);

        var jwtToken = jwtServiceImpl.generateToken(user);
        AuthenticationResponse goodReq = AuthenticationResponse.builder()
                .message("welcome " + user.getName())
                .token(jwtToken)
                .userDetails(userDTOMapper.apply(user))
                .build();
        return ResponseEntity.status(200).body(goodReq);
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
}
