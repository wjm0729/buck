/*
 * Copyright 2018-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.android.endtoend;

import com.facebook.buck.android.AssumeAndroidPlatform;
import com.facebook.buck.testutil.ProcessResult;
import com.facebook.buck.testutil.endtoend.EndToEndEnvironment;
import com.facebook.buck.testutil.endtoend.EndToEndRunner;
import com.facebook.buck.testutil.endtoend.EndToEndTestDescriptor;
import com.facebook.buck.testutil.endtoend.EndToEndWorkspace;
import com.facebook.buck.testutil.endtoend.Environment;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * E2E tests for buck's building process on an environment constructed like:
 *
 * <pre>
 *                                           android_binary
 *                                                  +
 *                    +-------------+---------------+----------------------------+
 *                    v             v                                            v
 *                 manifest     keystore                                  android_library
 *                    +                                                          +
 *                    |                    +------------------+----------------------------------------+-----------------+
 *                    v                    v                  v                  v                     v                 v
 *                 genrule          android_resource   android_library      prebuilt_jar     android_build_config   cxx_library
 *                    +                                       +                  +                                       +
 *    +---------------+                  +--------------------+                  |                                       |
 *    v               v                  v                    v                  v                                       v
 * genrule      python_binary       export_file     android_prebuilt_aar       genrule                               cxx_library
 *                                                                               +
 *                                                                               |
 *                                                                               v
 *                                                                         python_binary
 * </pre>
 */
@RunWith(EndToEndRunner.class)
public class AndroidEndToEndTest {
  private static final String mainTarget = "//android:demo-app";

  @Before
  public void assumeEnvironment() throws InterruptedException {
    AssumeAndroidPlatform.assumeSdkIsAvailable();
    AssumeAndroidPlatform.assumeNdkIsAvailable();
  }

  private static Map<String, Map<String, String>> getSourceABIConfigSet() {
    Map<String, Map<String, String>> configSet = new HashMap<>();
    configSet.put("java", new HashMap<>());
    configSet.get("java").put("source_level", "7");
    configSet.get("java").put("target_level", "7");
    configSet.get("java").put("track_class_usage", "true");
    configSet.get("java").put("compile_against_abis", "true");
    configSet.get("java").put("abi_generation_mode", "source_only");
    return configSet;
  }

  @Environment
  public static EndToEndEnvironment baseEnvironment() {
    return new EndToEndEnvironment()
        .addTemplates("mobile")
        .withCommand("build")
        .withTargets(mainTarget)
        .addLocalConfigSet(new HashMap<>())
        .addLocalConfigSet(getSourceABIConfigSet());
  }

  /** Determines that buck successfully outputs proper programs */
  @Test
  public void shouldBuild(
      EndToEndTestDescriptor test, EndToEndWorkspace workspace, ProcessResult result) {
    result.assertSuccess("Did not successfully build");
  }
}
