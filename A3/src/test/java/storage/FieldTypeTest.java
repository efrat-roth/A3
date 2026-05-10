package storage;

import org.junit.jupiter.api.Test;
import org.storage.FieldType;
import org.utils.Exceptions.InvalidFieldException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FieldTypeTest {

    @Test
    void constructorShouldThrowWhenFieldNameIsBlank() {
        assertThatThrownBy(() -> new FieldType(" ", String.class, 5, true, true, "hello"))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field name cannot be blank");
    }

    @Test
    void constructorShouldThrowWhenLengthIsNegative() {
        assertThatThrownBy(() -> new FieldType("title", String.class, -1, true, true, "hello"))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field length cannot be negative: -1");
    }

    @Test
    void constructorShouldThrowWhenContentIsNull() {
        assertThatThrownBy(() -> new FieldType("title", String.class, 5, true, true, null))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field content cannot be null");
    }
}
