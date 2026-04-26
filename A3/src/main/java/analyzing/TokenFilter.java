package analyzing;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TokenFilter {

    List<Token> apply(List<Token> tokens);
}
