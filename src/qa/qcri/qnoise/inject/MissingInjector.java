/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseGranularity;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.NoiseSpec;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.model.NoiseModel;
import qa.qcri.qnoise.util.Pair;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MissingInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    /** {@inheritDoc */
    @Override
    public MissingInjector inject(
        NoiseSpec spec,
        DataProfile dataProfile,
        NoiseReport report
    ) {
        HashSet<Pair<Integer, Integer>> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        NoiseModel model =
            NoiseModel.fromString(spec.<String>getValue(NoiseSpec.SpecEntry.Model));
        ModelBase randomModel = ModelFactory.createRandomModel();

        ModelBase indexGen;
        if (model == NoiseModel.Random) {
            indexGen = ModelFactory.createRandomModel();
        } else {
            String columnName = spec.getValue(NoiseSpec.SpecEntry.Column);
            indexGen = ModelFactory.createHistogramModel(dataProfile, columnName);
        }

        double perc = spec.getValue(NoiseSpec.SpecEntry.Percentage);
        List<String[]> data = dataProfile.getData();

        int len = (int)Math.floor(perc * data.size());
        report.addMetric(NoiseReport.Metric.ChangedItem, len);
        int count = 0;
        while(count < len) {
            int index = indexGen.nextIndex(0, data.size());
            NoiseGranularity granularity =
                NoiseGranularity.fromString(
                    (String)spec.getValue(NoiseSpec.SpecEntry.Granularity)
                );

            if (granularity == NoiseGranularity.CELL) {
                String[] rowData = data.get(index);
                int cellIndex = randomModel.nextIndex(0, rowData.length);
                Pair<Integer, Integer> record = new Pair<>(index, cellIndex);
                if (log.contains(record)) {
                    continue;
                }

                rowData[cellIndex] = null;
                report.logChange(index, cellIndex, "null");
                tracer.verbose(String.format("[%d, %d] <- null", index, cellIndex));
                log.add(record);
            } else {
                // set the whole tuple to missing
                String[] rowData = data.get(index);
                int width = dataProfile.getWidth();
                for (int i = 0; i < width; i ++) {
                    Pair<Integer, Integer> record = new Pair<>(index, i);
                    if (log.contains(record)) {
                        continue;
                    }

                    report.logChange(index, i, "null");
                    rowData[i] = null;
                    tracer.verbose(String.format("[%d, %d] <- null", index, i));;
                    log.add(record);
                }
            }
            count ++;
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        stopwatch.stop();
        return this;
    }
}