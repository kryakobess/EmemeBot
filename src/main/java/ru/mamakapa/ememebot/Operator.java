package ru.mamakapa.ememebot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mamakapa.ememebot.config.ImapConfig;
import ru.mamakapa.ememebot.service.email.EmailLetter;
import ru.mamakapa.ememebot.service.email.EmailService;
import ru.mamakapa.ememebot.service.sender.Sender;

import javax.mail.Message;
import java.util.List;
@Service
@Getter
@Slf4j
public class Operator {
    @Autowired
    private Sender sender;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ImapConfig imapConfig;
    private static final int TIME_SLEEP = 120000;
    @Value("${user.group_id}")
    private int USER_GROUP_ID;
    @Scheduled(fixedRate = TIME_SLEEP)

    public void run(){
        try {
            log.info("I'm listening...");
            if (!emailService.getEmailConnection().isConnected(imapConfig)) emailService.getEmailConnection().connectToEmail(imapConfig);
            List<Message> messages;
            try {
                messages = emailService.getEmailConnection().getLastMessages(imapConfig,
                        emailService.getEmailConnection().checkUpdates(imapConfig));
                if (messages != null) {
                    for (Message message : messages) {
                        EmailLetter letter = emailService.getEmailCompiler().constructLetter(message);
                        try {
                            sender.sendMessage(letter, USER_GROUP_ID);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        } finally {
                            emailService.deleteLetter(letter);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error connect! With exception: " + e.getMessage());
        }
    }
}
