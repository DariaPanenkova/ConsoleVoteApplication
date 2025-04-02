import App.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SurveyTest {

    private Survey survey;
    private List<String> options;

    @BeforeEach
    void setUp() {
        options = Arrays.asList("Вариант 1", "Вариант 2", "Вариант 3");
        survey =  new Survey.SurveyBuilder()
                .surveyName("Опрос")
                .description("Тестовый опрос")
                .loginCreator("admin")
                .maxOptions(3)
                .options(options)
                .build();
    }


    @Test
    @DisplayName("Test vote creation without name should throw exception")
    void testCreateVoteWithoutName() {
        assertThrows(IllegalStateException.class, () -> {
            new Survey.SurveyBuilder()
                    .description("Описание опроса")
                    .loginCreator("admin")
                    .addOption("Вариант 1")
                    .build();
        });

        assertThrows(IllegalStateException.class, () -> {
            new Survey.SurveyBuilder()
                    .surveyName("")
                    .description("Описание опроса")
                    .loginCreator("admin")
                    .addOption("Вариант 1")
                    .build();
        });

        assertThrows(IllegalStateException.class, () -> {
            new Survey.SurveyBuilder()
                    .surveyName("   ")
                    .description("Описание опроса")
                    .loginCreator("admin")
                    .addOption("Вариант 1")
                    .build();
        });
    }

}
