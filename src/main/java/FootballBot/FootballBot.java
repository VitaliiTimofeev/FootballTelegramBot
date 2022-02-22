package FootballBot;

import CurrencyBot.Currency;
import CurrencyBot.CurrencyBot;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FootballBot extends TelegramLongPollingBot {

    private final LeaguesInfoPressButton leaguesInfoPressButton = LeaguesInfoPressButton.getInstance();
    // private final CurrencyConvector currencyConvector = CurrencyConvector.getInstance();

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
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String parameter = callbackQuery.getData();
        System.out.println(parameter);
        if (parameter.contains("Назад League")){

            showLeagues(message, parameter.split(" ")[2]);
        } else if (parameter.contains("League")){
//            editChoose(parameter.split(" ")[1], message);
            leagueInfo(parameter.split(" ")[1], message);
        }
    }

    @SneakyThrows
    private void showLeagues(Message message, String parameter){

        Leagues newLeagues = Leagues.valueOf(parameter);

        leaguesInfoPressButton.setOriginalLeagues(message.getChatId(), newLeagues);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        Leagues originalLeagues = leaguesInfoPressButton.getOriginalLegues(message.getChatId());
        for (Leagues leagues : Leagues.values()) {
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(getLeaguesButton(originalLeagues, leagues)).callbackData("League " + leagues.toString()).build()

            ));

        }

        execute(SendMessage.builder()
                .text("Выберите лигу").chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build()
        );

//        leagueInfo(parameter, message);
        return;
    }

    @SneakyThrows
    private void editChoose(String parameter, Message message){
        System.out.println(parameter + " choose");
        Leagues newLeagues = Leagues.valueOf(parameter);
        leaguesInfoPressButton.setOriginalLeagues(message.getChatId(), newLeagues);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        Leagues originalLeagues = leaguesInfoPressButton.getOriginalLegues(message.getChatId());
        for (Leagues leagues : Leagues.values()) {
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(getLeaguesButton(originalLeagues, leagues)).callbackData("League " + leagues.toString()).build()

            ));

        }


        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build()
        );

    }
    @SneakyThrows
    private void leagueInfo(String name, Message message){
        System.out.println(name);
        String textBody = "This info about " + name;
//        switch (name){
//            case "PremierLeague":
//                textBody = "This info about PremierLeague";
//
//        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Cобытия").callbackData("Cобытия").build()

        ));
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Назад").callbackData("Назад League " + name).build()

        ));



        execute(SendMessage.builder().text(textBody)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());

    }

    private String getLeaguesButton(Leagues saved, Leagues current) {
        if (saved == current) {
            return current.name() + "✅";
        } else {
            return current.name();
        }
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities().stream()
                    .filter(e -> "bot_command".equals(e.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/show_leagues":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Leagues originalLeagues = leaguesInfoPressButton.getOriginalLegues(message.getChatId());
                        for (Leagues leagues : Leagues.values()) {
                            buttons.add(Arrays.asList(
                                    InlineKeyboardButton.builder()
                                            .text(getLeaguesButton(originalLeagues, leagues)).callbackData("League " + leagues.toString()).build()
                            ));

                        }
                        execute(SendMessage.builder()
                                .text("Выберите лигу").chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build()
                        );
                        return;
                }
            }

        }
       /* if (message.hasText()){
            String messageText = message.getText();
            Double value = Double.parseDouble(messageText);
            System.out.println(value);
            Leagues originalLeagues = leaguesInfoPressButton.getOriginalLegues(message.getChatId());
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
    } */


    }
}