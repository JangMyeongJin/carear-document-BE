package carear.document.be.mail.dto;

import lombok.Data;

@Data
public class MailRequestDto {
    private String name;
    private String email;
    private String title;
    private String content;

}
