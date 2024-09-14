package utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Functions {
    private static final String ACCENT_PATTERN = ".*[άέήίΐόΰύώ].*";
    private static final Map<Character, Character> VOWEL_AND_ITS_ACCENT = Map.of(
            'α', 'ά',
            'ε', 'έ',
            'η', 'ή',
            'ι', 'ί',
            'ο', 'ό',
            'υ', 'ύ',
            'ω', 'ώ');
    private static final Set<Character> VOWELS = Set.of('α', 'ε', 'η', 'ι', 'ο', 'υ', 'ω');

    private static final String COMMA = ", ";
    private static final String LEFT_PARENTHESIS = "(";
    private static final String RIGHT_PARENTHESIS = ")";
    private static final String SINGLE_QUOTE = "'";

    public static boolean isNotEmpty(String string) {
        return !(string == null || string.isBlank());
    }

    public static Set<String> generateAccentVariants(String input) {
        Set<String> variants = new HashSet<>();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            // If the current character is a vowel, replace it with its accented version
            if (VOWELS.contains(currentChar)) {
                StringBuilder variant = new StringBuilder(input);
                variant.setCharAt(i, VOWEL_AND_ITS_ACCENT.get(currentChar));
                variants.add(SINGLE_QUOTE + variant + SINGLE_QUOTE);
            }
        }

        return variants;
    }

    public static <T> String generateInClauseFor(Collection<T> parameters) {
        return parameters.stream().map(String::valueOf).collect(Collectors.joining(COMMA, LEFT_PARENTHESIS, RIGHT_PARENTHESIS));
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static boolean hasAccent(String word) {
        return word.matches(ACCENT_PATTERN);
    }
}
