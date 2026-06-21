package carear.document.be.mail.service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.mail.dto.MailRequestDto;
import carear.document.be.mail.dto.MailResponseDto;
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
    private String sendTo;

    @Async
    public ApiResponseDto sendMail(MailRequestDto mailDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(sendTo);
            message.setReplyTo(mailDto.getEmail());
            message.setTo(sendTo);  // 고정 수신자
            message.setSubject("[문의] MYCareer 메일보내기에서 보낸 메일");
            message.setText(
                    "이름: " + mailDto.getName() + "\n" +
                    "이메일: " + mailDto.getEmail() + "\n" +
                    "제목: " + mailDto.getTitle() + "\n" +
                    "내용: " + mailDto.getContent()
            );
            
            log.info("메일 전송 시도: {}", message.getSubject());
            mailSender.send(message);
            log.info("메일 전송 성공");

            MailResponseDto mailResponseDto = new MailResponseDto();
            mailResponseDto.setMessage("메일이 성공적으로 전송되었습니다.");
            
            return ApiResponseDto.success(mailResponseDto);
            
        } catch (Exception e) {
            log.error("메일 전송 실패: {}", e.getMessage(), e);
            
            return ApiResponseDto.fail("메일 전송에 실패했습니다: " + e.getMessage());
        }
    }
}
