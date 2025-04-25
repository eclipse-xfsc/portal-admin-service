package eu.gaiax.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailSendingService {

  private final JavaMailSender javaMailSender;
  @Value("${support.email}")
  private String senderMail;
  @Value("${send.email:true}")
  private boolean sendEmail;

  @Autowired
  public EmailSendingService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  public void sendEmailWithoutAttachments(final String emailTo, final String title, final String msgHTML) throws MessagingException {
    log.info("Sending email to: {}", emailTo);
    if (sendEmail) {
      final MimeMessage message = javaMailSender.createMimeMessage();
      final MimeMessageHelper helper = new MimeMessageHelper(message, false);
      helper.setSubject(title);
      helper.setFrom(senderMail);
      helper.setTo(emailTo);
      helper.setText(msgHTML, true);
      javaMailSender.send(message);
    } else {
      log.info("Email sending was switched off");
    }
  }
}
