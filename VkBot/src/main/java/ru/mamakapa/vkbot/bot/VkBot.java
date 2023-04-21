package ru.mamakapa.vkbot.bot;

import com.google.gson.Gson;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import org.springframework.stereotype.Service;
import ru.mamakapa.vkbot.bot.data.VkRecipient;
import ru.mamakapa.vkbot.bot.handler.CallbackHandler;
import ru.mamakapa.vkbot.config.VkBotConfig;
import ru.mamakapa.vkbot.service.MessageSender;
import ru.mamakapa.vkbot.service.UpdateHandler;

import java.util.Random;

@Service
public class VkBot implements MessageSender<VkRecipient, String>, UpdateHandler<String> {
    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;
    private final CallbackHandler callbackHandler;
    private final Random random = new Random();
    private final Gson gson = new Gson();
    public VkBot(VkBotConfig config) {
        this.vkApiClient = new VkApiClient(new HttpTransportClient());
        this.groupActor = new GroupActor(config.groupId(), config.token());
        this.callbackHandler = new CallbackHandler(config.callback().confirmationCode(), config.callback().secret()) {
            @Override
            protected void messageNew(Integer groupId, Message message) {
                try {
                    sendMessageText(message.getPeerId(), message.getText());
                } catch (ClientException | ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    @Override
    public String handle(String update) {
        return callbackHandler.parse(update);
    }

    @Override
    public void send(VkRecipient vkRecipient, String messageText) throws Exception{
        sendMessageText(vkRecipient.chatId(), messageText);
    }

    private void sendMessageText(int chatId, String messageText) throws ClientException, ApiException {
        this.vkApiClient.messages()
                .send(groupActor)
                .randomId(getRandomMessageId())
                .message(messageText)
                .peerId(chatId)
                .execute();
    }

    private int getRandomMessageId() {
        return random.nextInt(Integer.MAX_VALUE);
    }
}
