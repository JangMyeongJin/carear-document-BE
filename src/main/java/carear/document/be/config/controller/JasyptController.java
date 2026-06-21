package carear.document.be.config.controller;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jasypt")
public class JasyptController {

    private final StringEncryptor stringEncryptor;

    public JasyptController(@Qualifier("jasyptStringEncryptor") StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }

    @GetMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestParam String text) {
        System.out.println("text : " + text);
        String encrypted = "ENC(" + stringEncryptor.encrypt(text) + ")";
        return ResponseEntity.ok(encrypted);
    }
}
