package ru.mamakapa.vkbot.bot.function;

import org.springframework.stereotype.Service;
import ru.mamakapa.ememeSenderFunctionality.bot.EmemeBotFunctionality;
import ru.mamakapa.ememeSenderFunctionality.bot.data.EmailData;

import java.util.*;

@Service
public class VkEmemeBotFunctionality implements EmemeBotFunctionality {
    private final Map<Integer, List<EmailData>> users = new HashMap<>();

    @Override
    public void registerUser(int chatId) {
        if(!users.containsKey(chatId)){
            users.put(chatId, new ArrayList<>());
        }
    }

    @Override
    public void unregisterUser(int chatId) {
        users.remove(chatId);
    }

    @Override
    public void addEmail(int chatId, EmailData emailData) {
        if(users.containsKey(chatId)){
            users.get(chatId).add(emailData);
        }
    }

    @Override
    public List<EmailData> getAllEmails(int chatId) {
        return users.get(chatId);
    }

    @Override
    public void deleteEmail(int chatId, String address) {
        if(users.containsKey(chatId)){
            users.get(chatId).removeIf(emailData -> emailData.address().equals(address));
        }
    }
}
