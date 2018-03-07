package org.apache.hadoop.ozone.common.conf;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

public class ConfValidationRules {

  static class ValidatePath implements ConfValidationRule {
    public static void validate(String propVal) throws ValidationException {
      ConfValidationRule.validate(propVal);
      if(!isValidPath(propVal)){
        throw new ValidationException("Validation failed for " + propVal +
            ". Description: " + getValidationDesc());
      }
    }

    public static String getValidationDesc() {
      return "ValidatePath: Check if path is valid or not";
    }

    /**
     * Checks if a string is a valid path.
     * Null safe.
     */
    public static boolean isValidPath(String path) {
      try {
        Paths.get(path);
      } catch (InvalidPathException | NullPointerException ex) {
        return false;
      }
      return true;
    }
  }

  static class ValidateFile implements ConfValidationRule {
    public static void validate(String propVal) throws ValidationException {
      ConfValidationRule.validate(propVal);
      if(!isValidFile(propVal)) {
        throw new ValidationException("Validation failed for " + propVal +
            ". Description: " + getValidationDesc());
      }
    }

    public static String getValidationDesc() {
      return "ValidFile: Check if file exists in local file system.";
    }

    /**
     * Checks if a string is a valid path.
     * Null safe.
     */
    public static boolean isValidFile(String path) {
      try {
        return Files.exists(Paths.get(path));
      } catch (InvalidPathException | NullPointerException ex) {
        return false;
      }
    }
  }

  static class ValidateNumber implements ConfValidationRule {

    public static void validate(String propVal) throws ValidationException {
      ConfValidationRule.validate(propVal);
      if(!StringUtils.isNumeric(propVal)){
        throw new ValidationException("Validation failed for " + propVal +
            ". Description: " + getValidationDesc());
      }
    }

    public static String getValidationDesc() {
      return "ValidNumber: Check if value is numeric.";
    }
  }

}
