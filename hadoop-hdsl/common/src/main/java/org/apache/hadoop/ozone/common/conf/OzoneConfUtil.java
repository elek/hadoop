/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.common.conf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.ozone.common.conf.ConfValidationRules.ValidateFile;
import org.apache.hadoop.ozone.common.conf.ConfValidationRules.ValidateNumber;
import org.apache.hadoop.ozone.common.conf.ConfValidationRules.ValidatePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OzoneConfUtil {

  private final static Logger LOGGER = LoggerFactory
      .getLogger(OzoneConfUtil.class);
  private static final Map<String, Class<? extends ConfValidationRule>>
      CONF_RULE_MAP = new HashMap<>();

  static {
    CONF_RULE_MAP.put("REQUIRED", ConfValidationRule.class);
    CONF_RULE_MAP.put("NUMERIC", ValidateNumber.class);
    CONF_RULE_MAP.put("ISFILE", ValidateFile.class);
    CONF_RULE_MAP.put("ISPATH", ValidatePath.class);
  }

  public static List<String> validateConfiguration(Configuration conf, String
      validationTags) {
    final List<String> invalidConfigs = Collections.synchronizedList(new
        ArrayList<String>());
    for (String validationType : validationTags.split(",")) {
      conf.getAllPropertiesByTag(validationType.trim()).stringPropertyNames()
          .stream().forEach((String prop) -> {
        try {
          CONF_RULE_MAP.get(validationType.trim()).getDeclaredMethod
              ("validate", String.class).invoke(null, conf.get(prop));

        } catch (NoSuchMethodException | IllegalAccessException ex) {
        } catch (InvocationTargetException ex) {
          invalidConfigs.add(prop);
          LOGGER.error("Property: "+prop+" Cause:"+ex
              .getCause().getMessage());
        }
      });
    }
    return invalidConfigs;
  }

  public static List<String> validateConfiguration(Configuration conf) {
    return validateConfiguration(conf, conf.get(OzoneConfigKeys
        .OZONE_VALIDATION_TAGS));
  }

}