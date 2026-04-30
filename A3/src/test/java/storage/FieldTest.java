package storage;

import org.junit.jupiter.api.Test;
import org.storage.Field;
import org.utils.Exceptions.InvalidFieldException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FieldTest {

    @Test
    void constructorShouldThrowWhenFieldNameIsBlank() {
        assertThatThrownBy(() -> new Field(" ", String.class, 5, true, true, "hello"))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field name cannot be blank");
    }

    @Test
    void constructorShouldThrowWhenLengthIsNegative() {
        assertThatThrownBy(() -> new Field("title", String.class, -1, true, true, "hello"))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field length cannot be negative: -1");
    }

    @Test
    void constructorShouldThrowWhenContentIsNull() {
        assertThatThrownBy(() -> new Field("title", String.class, 5, true, true, null))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field content cannot be null");
    }
}
