import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TestBot2 extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "@test_13_2_22_1_bot";
    }

    @Override
    public String getBotToken() {
        return "5294749198:AAEb64iJks1Ghr5UDP9-ih-vGRxIX1RP1Z0";
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            if (message.hasText()){
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("You sent: \n\n" + message.getText())
                        .build());
            }
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        TestBot2 bot = new TestBot2();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
