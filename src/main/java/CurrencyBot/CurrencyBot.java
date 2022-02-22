package CurrencyBot;

import FootballBot.FootballBot;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class CurrencyBot extends TelegramLongPollingBot {

    private final CurrencyInfoPressButton currencyInfoPressButton = CurrencyInfoPressButton.getInstance();
    private final  CurrencyConvector currencyConvector = CurrencyConvector.getInstance();

    @Override
    public String getBotUsername() {
        return "@MyFootballScoreBot";
    }

    @Override
    public String getBotToken() {
        return "5159378587:AAHmjTrKnzgagW3tC58jBAMmuDxHD3Lh2zs";

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()){
            handleCallback(update.getCallbackQuery());
        }
        if (update.hasMessage()){
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] parameter = callbackQuery.getData().split(":");
        String action = parameter[0];
        Currency newCurrency = Currency.valueOf(parameter[1]);
        switch (action){
            case "ORIGINAL":
                currencyInfoPressButton.setOriginalCurrency(message.getChatId(), newCurrency);
                break;
            case "TARGET":
                currencyInfoPressButton.setTargetCurrency(message.getChatId(), newCurrency);
                break;
        }
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency originalCurrency = currencyInfoPressButton.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyInfoPressButton.getTargetCurrency(message.getChatId());
        for (Currency currency : Currency.values()){
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(getCurrencyButton(originalCurrency, currency)).callbackData("ORIGINAL:" + currency).build(),
                    InlineKeyboardButton.builder()
                            .text(getCurrencyButton(targetCurrency, currency)).callbackData("TARGET:" + currency).build()
            ));

        }
        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build()).build());
        return;
    }

    private String getCurrencyButton(Currency saved, Currency current){
        if (saved == current){
            return current.name()+"+";
        } else {
            return current.name();
        }
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()){
            Optional<MessageEntity> commandEntity = message.getEntities().stream()
                    .filter(e -> "bot_command".equals(e.getType())).findFirst();

            if (commandEntity.isPresent()){
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command){
                    case "/set_currency":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency originalCurrency = currencyInfoPressButton.getOriginalCurrency(message.getChatId());
                        Currency targetCurrency = currencyInfoPressButton.getTargetCurrency(message.getChatId());
                        for (Currency currency : Currency.values()){
                            buttons.add(Arrays.asList(
                                    InlineKeyboardButton.builder()
                                            .text(getCurrencyButton(originalCurrency, currency)).callbackData("ORIGINAL:" + currency).build(),
                                    InlineKeyboardButton.builder()
                                            .text(getCurrencyButton(targetCurrency, currency)).callbackData("TARGET:" + currency).build()
                                    ));

                        }
                        execute(SendMessage.builder()
                                .text("Выберите валюту").chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build()
                        );
                        return;
                }
            }

        }
        if (message.hasText()){
            String messageText = message.getText();
            Double value = Double.parseDouble(messageText);
            System.out.println(value);
            Currency originalCurrency = currencyInfoPressButton.getOriginalCurrency(message.getChatId());
            Currency targetCurrency = currencyInfoPressButton.getTargetCurrency(message.getChatId());
            double ratio = currencyConvector.getConversionRate(originalCurrency, targetCurrency);
            System.out.println(ratio);
            execute(SendMessage.builder().chatId(message.getChatId().toString())
                    .text(String.format("%4.2f %s is %4.2f %s", value, originalCurrency, value*ratio, targetCurrency)).build());
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        //CurrencyBot bot = new CurrencyBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        //telegramBotsApi.registerBot(bot);
        FootballBot bot1 = new FootballBot();
        telegramBotsApi.registerBot(bot1);
    }


}
