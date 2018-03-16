/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.sparky.search.registry;

import java.util.ArrayList;
import java.util.List;

import org.onap.aai.sparky.search.api.SearchProvider;

/**
 * Make this a java-scoped singleton to resolve the contextual issue spanning a Spring Context and
 * accessing the SPR in other parts of the code that are not directly instantiated by a Spring Bean.
 * Eventually the SPR doesn’t have to be a real singleton, it could simply be a Spring bean scoped
 * as a singleton and then wired in via dependency injection to the classes that need it. But I’m
 * not there yet. This will get a demonstrable extension mechanism in place quickly at practically
 * no cost, beyond what’s already in the email plus some testing.
 */

public class SearchProviderRegistry {

  private List<SearchProvider> searchProviders;

  public SearchProviderRegistry() {
    searchProviders = new ArrayList<SearchProvider>();
  }

  public List<SearchProvider> getSearchProviders() {
    return searchProviders;
  }

  public final void addSearchProvider(SearchProvider searchProvider) {

    if (searchProvider == null) {
      return;
    }

    if (!searchProviders.contains(searchProvider)) {
      searchProviders.add(searchProvider);
    }
  }

  public final void addSearchProviders(List<SearchProvider> searchProviders) {

    if (searchProviders == null) {
      return;
    }

    for (SearchProvider searchProvider : searchProviders) {
      addSearchProvider(searchProvider);
    }

  }

}
