package CurrencyBot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TestBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "@MyFootballScoreBot";
    }

    @Override
    public String getBotToken() {
        return "5159378587:AAHmjTrKnzgagW3tC58jBAMmuDxHD3Lh2zs";

    }

    @SneakyThrows
    public static void main(String[] args) {
        TestBot bot = new TestBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message != null && message.hasText()) {
                if (message.getText().equals("Привет")) {
                    System.out.println(message.getChatId().toString());
                    execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Привет я бот").build());
                } else {
                    execute(SendMessage.builder()
                            .chatId(message.getChatId().toString())
                            .text(message.getText()).build());
                }
            }
        }
    }

}
