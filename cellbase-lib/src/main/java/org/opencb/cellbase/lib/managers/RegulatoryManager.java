/*
 * Copyright 2015-2020 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.cellbase.lib.managers;

import org.opencb.cellbase.core.api.core.GeneDBAdaptor;
import org.opencb.cellbase.core.api.core.RegulationDBAdaptor;
import org.opencb.cellbase.core.config.CellBaseConfiguration;
import org.opencb.cellbase.core.result.CellBaseDataResult;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;

import java.util.List;

public class RegulatoryManager extends AbstractManager {

    private RegulationDBAdaptor regulationDBAdaptor;

    public RegulatoryManager(String species, String assembly, CellBaseConfiguration configuration) {
        super(species, assembly, configuration);
        this.init();
    }

    private void init() {
        regulationDBAdaptor = dbAdaptorFactory.getRegulationDBAdaptor(species, assembly);
    }


    public CellBaseDataResult getFeatureTypes(Query query) {
        return regulationDBAdaptor.distinct(query, "featureType");
    }

    public CellBaseDataResult getFeatureClasses(Query query) {
        return regulationDBAdaptor.distinct(query, "featureClass");
    }

    public CellBaseDataResult search(Query query, QueryOptions queryOptions) {
        return regulationDBAdaptor.nativeGet(query, queryOptions);
    }

    public List<CellBaseDataResult> getByRegions(Query query, QueryOptions queryOptions, String regions) {
        List<Query> queries = createQueries(query, regions, RegulationDBAdaptor.QueryParams.REGION.key());
        List<CellBaseDataResult> queryResults = regulationDBAdaptor.nativeGet(queries, queryOptions);
        for (int i = 0; i < queries.size(); i++) {
            queryResults.get(i).setId((String) queries.get(i).get(RegulationDBAdaptor.QueryParams.REGION.key()));
        }
        return queryResults;
    }

    public List<CellBaseDataResult> getAllByTfbs(Query query, QueryOptions queryOptions, String tf) {
        List<Query> queries = createQueries(query, tf, RegulationDBAdaptor.QueryParams.NAME.key(),
                RegulationDBAdaptor.QueryParams.FEATURE_TYPE.key(), RegulationDBAdaptor.FeatureType.TF_binding_site
                        + "," + RegulationDBAdaptor.FeatureType.TF_binding_site_motif);
        List<CellBaseDataResult> queryResults = regulationDBAdaptor.nativeGet(queries, queryOptions);
        for (int i = 0; i < queries.size(); i++) {
            queryResults.get(i).setId((String) queries.get(i).get(RegulationDBAdaptor.QueryParams.NAME.key()));
        }
        return queryResults;
    }

    public List<CellBaseDataResult> getTfByRegions(Query query, QueryOptions queryOptions, String regions) {
        if (hasHistogramQueryParam(queryOptions)) {
            List<Query> queries = createQueries(query, regions, GeneDBAdaptor.QueryParams.REGION.key());
            List<CellBaseDataResult> queryResults = regulationDBAdaptor.getIntervalFrequencies(queries,
                    getHistogramIntervalSize(queryOptions), queryOptions);
            for (int i = 0; i < queries.size(); i++) {
                queryResults.get(i).setId((String) query.get(GeneDBAdaptor.QueryParams.REGION.key()));
            }
            return queryResults;
        } else {
            List<Query> queries = createQueries(query, regions, RegulationDBAdaptor.QueryParams.REGION.key(),
                    RegulationDBAdaptor.QueryParams.FEATURE_TYPE.key(),
                    RegulationDBAdaptor.FeatureType.TF_binding_site + ","
                            + RegulationDBAdaptor.FeatureType.TF_binding_site_motif);
            List<CellBaseDataResult> queryResults = regulationDBAdaptor.nativeGet(queries, queryOptions);
            for (int i = 0; i < queries.size(); i++) {
                queryResults.get(i).setId((String) queries.get(i).get(RegulationDBAdaptor.QueryParams.REGION.key()));
            }
            return queryResults;
        }
    }
}
