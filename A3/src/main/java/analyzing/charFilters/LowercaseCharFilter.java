package analyzing.charFilters;
//For thinking - which type will be the return value, what design pattern is the solution? decorator, builder...
//anlyzer.charfilters.stream.forEach

public class LowercaseCharFilter implements CharFilter {

    @Override
    public String apply(String input) {
        input = input.toLowerCase();
        return input;
    }
}
