package onim.en.hmage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;

public class HMageSettings {

  public static boolean enabled = true;

  private static Path config;
  private static Properties properties;

  public static KeyBinding openSettingsKey = new KeyBinding("hmage.key.settings", GLFW.GLFW_KEY_P,
      "key.categories.hmage");

  public static KeyBinding showAnniRankingTab = new KeyBinding("hmage.key.anni-ranking", GLFW.GLFW_KEY_H,
      "key.categories.hmage");

  public static void init(Path config) {
    HMageSettings.config = config;
    try {
      HMage.LOGGER.info("trying to create configuration file");
      Files.createFile(config);
    } catch (IOException e) {
      if (e instanceof FileAlreadyExistsException) {
        HMage.LOGGER.info(e.getMessage());
      } else {
        e.printStackTrace();
      }
    }

    properties = new Properties();

    try {
      properties.load(Files.newInputStream(config));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void save() {
    if (config == null)
      return;

    setBoolean("enabled", enabled);

    new Thread(() -> {
      try {
        properties.store(Files.newBufferedWriter(config, StandardCharsets.UTF_8), "Created by HMage");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();

  }

  public static void enable() {
    enabled = true;
    setBoolean("enabled", enabled);
  }

  public static void disable() {
    enabled = false;
    setBoolean("enabled", enabled);
  }

  public static void setProperty(String key, String value) {
    if (properties == null)
      return;
    properties.setProperty(key, value);
  }

  public static boolean getBoolean(String key, boolean defaultValue) {
    if (properties == null)
      return defaultValue;
    return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
  }

  public static void setBoolean(String key, boolean value) {
    if (properties == null)
      return;
    properties.setProperty(key, String.valueOf(value));
  }

  public static String getString(String key, String defaultValue) {
    if (properties == null)
      return defaultValue;
    return properties.getProperty(key, String.valueOf(defaultValue));
  }

  public static void setString(String key, String value) {
    if (properties == null)
      return;
    properties.setProperty(key, value);
  }

  public static int getInt(String key, int defaultValue) {
    if (properties == null)
      return defaultValue;
    try {
      return Integer.valueOf(properties.getProperty(key, String.valueOf(defaultValue)));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public static void setInt(String key, int value) {
    if (properties == null)
      return;
    properties.setProperty(key, String.valueOf(value));
  }

  public static float getFloat(String key, float defaultValue) {
    if (properties == null)
      return defaultValue;
    try {
      return Float.parseFloat(properties.getProperty(key, String.valueOf(defaultValue)));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static void setFloat(String key, float value) {
    if (properties == null)
      return;
    properties.setProperty(key, String.valueOf(value));
  }

}
