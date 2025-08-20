package carear.document.be.mail.controller;

import carear.document.be.mail.dto.MailDto;
import carear.document.be.mail.service.MailSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailSendController {

    private final MailSendService mailSendService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody MailDto mailDto) {
        try {
            log.info("[MailSend] mailDto : " + mailDto);
            boolean result = mailSendService.sendMail(mailDto);
            
            if (result) {
                return ResponseEntity.ok("메일이 성공적으로 전송되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("메일 전송에 실패했습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("메일 전송에 실패했습니다: " + e.getMessage());
        }
    }
}
