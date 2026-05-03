package org.analyzing.charFilters;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class CharFilterProvider {

    private final CharFilterRegistry charFilterRegistry = new CharFilterRegistry();

    public  List<CharFilter> provide(List<String> names) {

        if (names == null) {
            throw new Exceptions.AnalyzerConfigurationException(
                    "Char filter configuration is null"
            );
        }

        List<CharFilter> filters = new ArrayList<>();

        for (String name : names) {

            CharFilter charFilter = charFilterRegistry.get(name);

            if (charFilter == null) {
                throw new Exceptions.AnalyzerNotFoundException(
                        "Unknown char filter: " + name
                );
            }

            filters.add(charFilter);

            log.debug("Char filter created: {}", name);
        }

        return filters;
    }
}
