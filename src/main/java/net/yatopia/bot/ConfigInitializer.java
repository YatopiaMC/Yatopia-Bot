package net.yatopia.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public class ConfigInitializer {

  private ObjectNode object;
  private boolean shouldStart = true;

  public ConfigInitializer() {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File("config.json");
    if (!file.exists()) {
      shouldStart = false;
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      ObjectNode node = mapper.getNodeFactory().objectNode();
      node.put("token", "Your token here");
      try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, node);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!shouldStart) {
      return;
    }
    try (Reader reader = new FileReader(file)) {
      object = (ObjectNode) mapper.readTree(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getToken() {
    return object.get("token").asText();
  }

  public boolean shouldStart() {
    return shouldStart;
  }
}
