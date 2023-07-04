package org.dobrynin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) {

    for (String arg : args) {
      removeCopies(arg);
    }
  }

  private static void removeCopies(String path) {
    List<Path> candidatesForRemovingList = new ArrayList<>();
    Map<Long, List<Path>> mapWithCopies = getMapWithCopies(getMapWithAllFiles(path));
    for (Map.Entry<Long, List<Path>> entry : mapWithCopies.entrySet()) {
      List<Path> rawList = entry.getValue();
      rawList.sort(Comparator.comparing(h -> h.toFile().lastModified()));
      rawList.remove(rawList.size() - 1);
      candidatesForRemovingList.addAll(rawList);
    }

    candidatesForRemovingList.forEach(path1 -> {
      try {
        Files.deleteIfExists(path1);
      } catch (IOException e) {
        e.printStackTrace();
      }

    });
  }

  private static Map<Long, List<Path>> getMapWithAllFiles(String path) {
    try (Stream<Path> stream = Files.walk(Path.of(path))) {
      return stream
              .filter(path1 -> path1.toFile().isFile())
              .collect(groupingBy(path2 -> path2.toFile().length()));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Map<Long, List<Path>> getMapWithCopies(Map<Long, List<Path>> mapOfAllFiles) {
    return mapOfAllFiles
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().size() > 1)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }


}