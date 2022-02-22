import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TestBot extends DefaultAbsSender {
    protected TestBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return "5294749198:AAEb64iJks1Ghr5UDP9-ih-vGRxIX1RP1Z0";
    }

    @SneakyThrows
    public static void main(String[] args) {
        TestBot testBot = new TestBot(new DefaultBotOptions());
        testBot.execute(SendMessage.builder().chatId("429272623").text("Hello World from Java").build());
    }
}
