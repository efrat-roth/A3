package analyzing.tokenFilters;

import analyzing.Token;

import java.io.IOException;
import java.util.List;

public interface TokenFilter {

    List<Token> apply(List<Token> tokens) throws IOException;
}
