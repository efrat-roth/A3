package org.analyzing.charFilters;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.List;

@Slf4j
@NoArgsConstructor
public class CharFilterProvider {

    private final CharFilterRegistry charFilterRegistry = new CharFilterRegistry();

    public List<CharFilter> provide(List<String> names) {

        if (names == null) {
            throw new Exceptions.AnalyzerConfigurationException("Char filter configuration is null");
        }

        return names.stream()
                .map(charFilterRegistry::get)
                .toList();
    }
}
