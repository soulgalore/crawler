/******************************************************
 * Web crawler
 * 
 * 
 * Copyright (C) 2013 by Peter Hedenskog (http://peterhedenskog.com)
 * 
 ****************************************************** 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 ******************************************************* 
 */
package com.soulgalore.crawler.core.assets;

import java.util.Set;

/**
 * Holds the result of an assets verification.
 */
public class AssetsVerificationResult {

  private final Set<AssetResponse> nonWorkingAssets;
  private final Set<AssetResponse> workingAssets;

  public AssetsVerificationResult(Set<AssetResponse> theWorkingAssets,
      Set<AssetResponse> theNonWorkingAssets) {
    nonWorkingAssets = theNonWorkingAssets;
    workingAssets = theWorkingAssets;
  }

  public Set<AssetResponse> getNonWorkingAssets() {
    return nonWorkingAssets;
  }

  public Set<AssetResponse> getWorkingAssets() {
    return workingAssets;
  }

}
