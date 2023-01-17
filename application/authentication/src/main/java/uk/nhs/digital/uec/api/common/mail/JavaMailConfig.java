package uk.nhs.digital.uec.api.common.mail;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Profile("mail-smtp")
@Slf4j
public class JavaMailConfig {

    @Value("${mail.host:localhost}")
    private String host;

    @Value("${mail.port:587}")
    private int port;

    @Value("${mail.username:}")
    private String username;

    @Value("${mail.password:}")
    private String password;

    @Value("${mail.properties.mail.transport.protocol:smtp}")
    private String protocol;

    @Value("${mail.properties.mail.smtp.auth:true}")
    private boolean auth;

    @Value("${mail.properties.mail.smtp.debug:false}")
    private boolean debug;

    @Value("${mail.properties.mail.smtp.starttls.enable:true}")
    private boolean startTlsEnable;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        log.info("SMTP host = " + host + ", port = " + port + ", username = " + username);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", auth);
        props.put("mail.debug", debug);
        props.put("mail.smtp.starttls.enable", startTlsEnable);

        return javaMailSender;
    }

}
