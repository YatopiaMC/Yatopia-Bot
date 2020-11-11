package net.yatopia.bot.message.filters;

import java.util.Arrays;
import java.util.List;

public class AsciiCyrillicCharactersFilter implements MessageFilter {

  private final List<Character> allowedChars =
      Arrays.asList(
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
          'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
          'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с',
          'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ь', 'ъ', 'ѝ', 'ю', 'я', 'є', 'ї', 'і', 'ß', 'ы',
          'э', 'ä', 'ö', '©', '®', '~', '#', '@', '$', '%', '^', '&', '*', '(', ')', '-', '+', '/',
          '\\', '`', '!', '?', ',', '<', '>', '€', '.', '\'', '"', ':', ';', '{', '}', '|', '[',
          ']', '«', '¬', '»', '–', '§', ' ', '=', 'Ì');

  @Override
  public void apply(FilterContext context) {
    String contentRaw = context.getMessage().getContentRaw().toLowerCase();
    for (char c : contentRaw.toCharArray()) {
      if (!allowedChars.contains(c)) {
        context.getMessage().delete().queue();
        context.respond(
            context.getAuthor().getAsMention() + " , don't use characters that are not allowed");
        break;
      }
    }
  }
}
