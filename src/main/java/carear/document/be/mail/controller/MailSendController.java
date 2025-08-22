package carear.document.be.mail.controller;

import carear.document.be.mail.dto.MailRequestDto;
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
    public ResponseEntity<?> sendMail(@RequestBody MailRequestDto mailDto) {
        log.info("[MailSend] mailDto : " + mailDto);

        return ResponseEntity.ok(mailSendService.sendMail(mailDto));
    }
}
