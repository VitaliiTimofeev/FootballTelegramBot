package FootballBot;


import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.K;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.security.auth.kerberos.KerberosKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FootballBot extends TelegramLongPollingBot {
    private final DataBaseFootball db = new DataBaseFootball();
    private final LeaguesInfoPressButton leaguesInfoPressButton = LeaguesInfoPressButton.getInstance();

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
        if (update.hasCallbackQuery()) {                                 //нажатие на кнопку
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
        if (parameter.contains("BackToLeague")) {
            showLeagues(message, parameter.split(" ")[1]);
        } else if (parameter.contains("LeagueInfo")) {
            editChoose(parameter.split(" ")[1], message);
            leagueInfo(parameter.split(" ")[1], message);
        } else if (parameter.contains("Бомбардиры")) {
            showBombardiers(parameter.split(" ")[1], message);
        } else if (parameter.contains("События")) {
            showCalendar(parameter.split(" ")[1], message);
        } else if (parameter.contains("Добавить в избранное")){
           showTeams(parameter.split(" ")[3], message);
        } else if (parameter.contains("FavoriteTeam")){
            selectFavorites ( Arrays.copyOfRange(parameter.split(" "), 1, parameter.split(" ").length), message);
        } else if (parameter.contains("BackToStart")){
            execute(SendMessage.builder()
                    .text("Футбол")
                    .chatId(message.getChatId().toString())
                    .replyMarkup(ReplyKeyboardMarkup.builder().keyboard(menuPanel()).build())
                    .build());
        } else if (parameter.contains("FTExist")){
            showFavoriteTeam(parameter.split(" ")[2], message);
        }
    }

    @SneakyThrows
    private void showFavoriteTeam(String teamName, Message message) {
        ConnectApi connectApi = new ConnectApi();
        String textBody = connectApi.getTeamsInfo(teamName);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Удалить команду из избранных").callbackData("DeleteFromFavorite " + teamName).build()
        ));

        execute(SendMessage.builder().text(textBody)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());

    }


    @SneakyThrows
    private void selectFavorites(String[] teamArray, Message message) {
        StringBuilder teamStringBuilder = new StringBuilder();
        for (String word : teamArray){
            teamStringBuilder.append(word + " ");
        }
        String team = teamStringBuilder.substring(0, teamStringBuilder.toString().length() - 1);
        db.addTeam(message.getChatId(), team);
        execute(SendMessage.builder().text("Команда " + team + " добавлена в избранное")
                .chatId(message.getChatId().toString())
                .build());

    }

    @SneakyThrows
    private void showTeams(String nameLeague, Message message) {
        ConnectApi connectApi = new ConnectApi();
        List<String> teams = connectApi.getFavorite(Leagues.getIdByName(nameLeague));
        String textBody = "Выберите команду, чтобы добавить в избранные";
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (String team : teams){
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(team).callbackData("FavoriteTeam " + team).build()
            ));
        }

        execute(SendMessage.builder().text(textBody)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }


    @SneakyThrows
    private void showMatches(Message message) {
        ConnectApi connectApi = new ConnectApi();
        String textBody = "Матчи сегодня:\n\n" + connectApi.getMatches();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();


        execute(SendMessage.builder().text(textBody)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }







    @SneakyThrows
    private void showCalendar(String name, Message message) {
        String textBody = "События:\n\n";
        System.out.println(textBody + " " + name);
        ConnectApi connectApi = new ConnectApi();
        switch (name) {
            case "PremierLeague":
                textBody += connectApi.getCalendar(Leagues.PremierLeague.getId());
                break;
            case "BundesLiga":
                textBody += connectApi.getCalendar(Leagues.BundesLiga.getId());
                break;
            case "LaLiga":
                textBody += connectApi.getCalendar(Leagues.LaLiga.getId());
                break;
            case "SerieA":
                textBody += connectApi.getCalendar(Leagues.SerieA.getId());
                break;
            case "Ligue1":
                textBody += connectApi.getCalendar(Leagues.Ligue1.getId());
                break;
        }
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Назад").callbackData("BackToLeagueInfo " + name).build()

        ));

        execute(SendMessage.builder().text(textBody)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

    @SneakyThrows
    private void showBombardiers(String name, Message message) {
        String textBody = "Bombardiers:\n";

        ConnectApi connectApi = new ConnectApi();
        switch (name){
            case "PremierLeague":
                textBody += connectApi.getBombardiers(Leagues.PremierLeague.getId());
                break;
            case "BundesLiga":
                textBody += connectApi.getBombardiers(Leagues.BundesLiga.getId());
                break;
            case "LaLiga":
                textBody += connectApi.getBombardiers(Leagues.LaLiga.getId());
                break;
            case "SerieA":
                textBody += connectApi.getBombardiers(Leagues.SerieA.getId());
                break;
            case "Ligue1":
                textBody += connectApi.getBombardiers(Leagues.Ligue1.getId());
                break;
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Назад").callbackData("BackToLeague " + name).build()

        ));

        execute(SendMessage.builder().text(textBody)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }


    @SneakyThrows
    private void showLeagues(Message message, String parameter) {
        System.out.println("ShowLeagues");
        Leagues newLeagues = Leagues.valueOf(parameter);

        leaguesInfoPressButton.setOriginalLeagues(message.getChatId(), newLeagues);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        Leagues originalLeagues = leaguesInfoPressButton.getOriginalLegues(message.getChatId());
        for (Leagues leagues : Leagues.values()) {
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(getLeaguesButton(originalLeagues, leagues)).callbackData("LeagueInfo " + leagues.toString()).build()

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
    private void editChoose(String parameter, Message message) {
        Leagues newLeagues = Leagues.valueOf(parameter);
        leaguesInfoPressButton.setOriginalLeagues(message.getChatId(), newLeagues);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        Leagues originalLeagues = leaguesInfoPressButton.getOriginalLegues(message.getChatId());
        for (Leagues leagues : Leagues.values()) {
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(getLeaguesButton(originalLeagues, leagues)).callbackData("LeagueInfo " + leagues.toString()).build()

            ));

        }


        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString()).messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build()
        );

    }




    @SneakyThrows
    private void leagueInfo(String name, Message message) {
        System.out.println(name);
        String textBody = "This info about " + name + "\n\n";

        ConnectApi connectApi = new ConnectApi();
        switch (name){
            case "PremierLeague":
                textBody += connectApi.getRating(Leagues.PremierLeague.getId());
                break;
            case "BundesLiga":
                textBody += connectApi.getRating(Leagues.BundesLiga.getId());
                break;
            case "LaLiga":
                textBody += connectApi.getRating(Leagues.LaLiga.getId());
                break;
            case "SerieA":
                textBody += connectApi.getRating(Leagues.SerieA.getId());
                break;
            case "Ligue1":
                textBody += connectApi.getRating(Leagues.Ligue1.getId());
                break;

        }



        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Бомбардиры").callbackData("Бомбардиры " + name).build(),
                InlineKeyboardButton.builder()
                        .text("События").callbackData("События " + name).build()

        ));
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Добавить в избранное").callbackData("Добавить в избранное " + name).build()

        ));

        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("Назад").callbackData("BackToLeague " + name).build()

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

    private ArrayList<KeyboardRow> menuPanel() {
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();

        keyboardRow1.add("\u26BD Лиги");
        keyboardRow1.add("\uD83D\uDD25 Избранное");
        keyboardRow2.add("\uD83D\uDCC3 Матчи сегодня");
        keyboardRow3.add("\uD83D\uDCF0 Футбольные новости");

        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);


        return keyboardRows;
    }


    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities().stream()
                    .filter(e -> "bot_command".equals(e.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/start":
                        db.insertUser(message.getChatId().intValue());
                        execute(SendMessage.builder()
                                .text("Футбол")
                                .chatId(message.getChatId().toString())
                                .replyMarkup(ReplyKeyboardMarkup.builder().keyboard(menuPanel()).build())
                                .build());
                        break;
                    case "/show_news":
                        execute(SendMessage.builder().text("Важные новости").chatId(message.getChatId().toString()).build());
                        //TODO
                        break;
                    case "/set_favorite":
                        execute(SendMessage.builder().text("Избранное").chatId(message.getChatId().toString()).build());
                        //TODO
                        break;
                    case "/show_matchs":
                        showMatches(message);

                        break;
                    case "/show_leagues":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Leagues originalLeagues = leaguesInfoPressButton.getOriginalLegues(message.getChatId());
                        for (Leagues leagues : Leagues.values()) {
                            buttons.add(Arrays.asList(
                                    InlineKeyboardButton.builder()
                                            .text(leagues.toString()).callbackData("LeagueInfo " + leagues.toString()).build()
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

        if (message.hasText()) {
            String messageText = message.getText();
            System.out.println(messageText);
            switch (messageText) {
                case "\uD83D\uDCF0 Футбольные новости":
                    execute(SendMessage.builder().text("Важные новости").chatId(message.getChatId().toString()).build());
                    break;
                case "\uD83D\uDD25 Избранное":
                    List<List<InlineKeyboardButton>> buttonsTeams = new ArrayList<>();
                    String textBody;
                    if (db.selectTeams(message.getChatId())[0].equals("")){
                        textBody = "У вас пока нет команд в избранных";
                    } else {
                        textBody = "Избранные команды";
                        for (String team : db.selectTeams(message.getChatId())){
                            buttonsTeams.add(Arrays.asList(
                                    InlineKeyboardButton.builder()
                                            .text(team).callbackData(message.getChatId().toString() + " FTExist " + team).build()
                            ));
                        }

                    }
                    buttonsTeams.add(Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text("Назад").callbackData("BackToStart").build()
                    ));
                    execute(
                            SendMessage.builder().text(textBody).chatId(message.getChatId().toString())
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttonsTeams).build())
                                    .build());
                    break;
                case "\uD83D\uDCC3 Матчи сегодня":
                    showMatches(message);
                    break;
                case "\u26BD Лиги":
                    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                    for (Leagues leagues : Leagues.values()) {
                        buttons.add(Arrays.asList(
                                InlineKeyboardButton.builder()
                                        .text(leagues.toString()).callbackData("LeagueInfo " + leagues.toString()).build()
                        ));

                    }
                    execute(SendMessage.builder()
                            .text("Выберите лигу").chatId(message.getChatId().toString())
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                            .build()
                    );
                    break;
            }
        }

    }


    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        FootballBot bot1 = new FootballBot();
        telegramBotsApi.registerBot(bot1);
    }

    }
