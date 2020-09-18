/*
 * Copyright 2015 OpenCB
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

package org.opencb.cellbase.app.cli;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.mortbay.util.ajax.JSON;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.PopulationFrequency;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by imedina on 03/02/15.
 */
public class RebuildVariantCommandExecutor extends CommandExecutor {


    public RebuildVariantCommandExecutor(CliOptionsParser.RebuildVariantCommandOptions commandOptions) {
//        super(loadCommandOptions.commonOptions.logLevel, loadCommandOptions.commonOptions.verbose,
//                loadCommandOptions.commonOptions.conf);
    }


    /**
     * Parse specific 'data' command options.
     */
    public void execute() {

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        ObjectWriter jsonObjectWriter = jsonObjectMapper.writer();
        jsonObjectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            BufferedWriter bufferedWriter = FileUtils.newBufferedWriter(Paths.get("/data/new_variants/chrMT.json"));
            BufferedReader bufferedReader = FileUtils.newBufferedReader(Paths.get("/data/variation/variation_chrMT.json.gz"));


            String line = bufferedReader.readLine();
            while (line != null) {
                Variant oldVariant = jsonObjectMapper.convertValue(JSON.parse(line), Variant.class);
                VariantAnnotation variantAnnotation = new VariantAnnotation();
                List<PopulationFrequency> populationFrequencies = oldVariant.getAnnotation().getPopulationFrequencies();
                variantAnnotation.setPopulationFrequencies(populationFrequencies);
                Variant newVariant = oldVariant;
                newVariant.setAnnotation(variantAnnotation);
                bufferedWriter.write(jsonObjectWriter.writeValueAsString(newVariant));
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
