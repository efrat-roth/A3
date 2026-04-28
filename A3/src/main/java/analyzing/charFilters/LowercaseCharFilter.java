package analyzing.charFilters;

public class LowercaseCharFilter implements CharFilter {

    @Override
    public String apply(String input) {
        input = input.toLowerCase();
        return input;
    }
}
