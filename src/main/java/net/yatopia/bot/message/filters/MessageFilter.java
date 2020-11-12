package net.yatopia.bot.message.filters;

import java.util.List;
import net.yatopia.bot.util.Utils;

public interface MessageFilter {

  void apply(FilterContext context);

  default boolean applyEffectiveFilter(String messageContent, String word) {
    if (messageContent.isEmpty()) {
      return false;
    }
    word = word.toLowerCase();
    String messageRaw = messageContent.toLowerCase().replace(",", "").replace(".", "");
    if (messageRaw.contains(word)) {
      return true;
    }

    // check if the word is separated by spaces
    String[] parts = messageRaw.split(" ");
    String previousPart = null;
    for (String part : parts) {
      if (previousPart == null) {
        previousPart = part;
        continue;
      }
      String partsTogether = previousPart + part; // :)
      if (partsTogether.equalsIgnoreCase(word)) {
        return true;
      }
      previousPart = part;
    }

    // didn't think someone would go _that_ far
    int wordLength = word.length();
    char[] wordSequence = word.toCharArray();
    messageRaw = messageRaw.replace(" ", "");
    StringReader stringReader = new StringReader(messageRaw);
    while (stringReader.canRead(wordLength)) {
      char[] sequence = stringReader.readNextChars(wordLength);
      int matches = 0;
      for (int i = 0; i < wordLength; i++) {
        char wordSeqC = wordSequence[i];
        char seqC = sequence[i];
        if (wordSeqC == seqC) {
          matches++;
          continue;
        }
        if ((i + 1) < wordLength) {
          char seqC1 = sequence[i + 1];
          if (wordSeqC == seqC1) {
            matches++;
          }
        }
      }
      if (matches >= wordLength) {
        return true;
      }
    }

    // check the rest of the word ( in case we didn't find a word already and
    // stringReader.canRead(wordLength) is false )
    int vl = 0;
    int charsRead = 0;
    while (stringReader.canRead()) {
      char c = stringReader.readNextChar();
      for (char wordSeqC : wordSequence) {
        if (c == wordSeqC) {
          vl++;
        }
      }
      charsRead++;
    }
    return vl >= charsRead; // intellij forced me to do this 
  }

  default boolean applyEffectiveFilter(
      String messageContent, int threshold, List<String> wordSequence) {
    if (messageContent.isEmpty()) {
      return false;
    }
    String messageRaw = messageContent.toLowerCase().replace(".", "").replace(",", "");
    StringBuilder concatenate = new StringBuilder();
    for (String word : wordSequence) {
      concatenate.append(word.toLowerCase()).append(' ');
    }
    String concatenated = concatenate.substring(0, concatenate.length() - 1);
    if (messageRaw.contains(concatenated)) {
      // bruh moment
      // although this would definitely fail if more words are specified
      return true;
    }
    int containMatches = 0;
    String previousWord = null;
    for (String word : wordSequence) {
      if (Utils.containsSingleWord(messageRaw, word)) {
        if (previousWord != null) {
          if (previousWord.contains(word)) {
            // make sure stuff like "squids" and "squid" don't end up being after each other
            continue;
          }
        }
        containMatches++;
        previousWord = word;
      }
      if (containMatches == threshold) {
        return true;
      }
    }

    // time for chars :)
    messageRaw = messageRaw.replace(" ", "");
    int charMatches = 0;
    for (String word : wordSequence) {
      int wordLength = word.length();
      char[] wSequence = word.toLowerCase().toCharArray();
      StringReader stringReader = new StringReader(messageRaw);
      while (stringReader.canRead(wordLength)) {
        char[] sequence = stringReader.readNextChars(wordLength);
        int matches = 0;
        for (int i = 0; i < wordLength; i++) {
          char wordSeqC = wSequence[i];
          char seqC = sequence[i];
          if (wordSeqC == seqC) {
            matches++;
            continue;
          }
          if ((i + 1) < wordLength) {
            char seqC1 = sequence[i + 1];
            if (wordSeqC == seqC1) {
              matches++;
            }
          }
        }
        if (messageRaw.indexOf(' ') == -1) {
          if (matches >= messageRaw.length()) {
            charMatches++;
          }
        } else if (matches >= wordLength) {
          charMatches++;
        }
      }
      // at this point, stringReader.canRead(wordLength) is false
      // we're just gonna check the rest of the string
      int matches = 0;
      while (stringReader.canRead()) {
        char c = stringReader.readNextChar();
        for (char wC : wSequence) {
          if (wC == c) {
            matches++;
          }
        }
      }
      if (matches >= wordLength) {
        charMatches++;
      }
      if (charMatches == threshold) {
        return true;
      }
    }
    // all good
    return false;
  }
}
