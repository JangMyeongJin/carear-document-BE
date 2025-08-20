package carear.document.be.mail.service;

import carear.document.be.mail.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSendService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.to}")
    private String to;

    @Async
    public boolean sendMail(MailDto mailDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailDto.getEmail());  // 고정 수신자
            message.setSubject("[문의] " + mailDto.getTitle());
            message.setText(
                "이름: " + mailDto.getName() + "\n" +
                "내용: " + mailDto.getContent()
            );
            
            log.info("메일 전송 시도: {}", message.getSubject());
            mailSender.send(message);
            log.info("메일 전송 성공");
            
            return true;
            
        } catch (Exception e) {
            log.error("메일 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메일 전송에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
