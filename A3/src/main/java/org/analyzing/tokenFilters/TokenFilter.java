package org.analyzing.tokenFilters;

import org.analyzing.Token;

import java.io.IOException;
import java.util.List;

public interface TokenFilter {

    void apply(List<Token> tokens) throws IOException;
}
