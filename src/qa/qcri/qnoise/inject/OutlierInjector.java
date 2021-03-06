/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.javatuples.Pair;
import qa.qcri.qnoise.internal.*;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OutlierInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    @Override
    public void act(
        NoiseContext context,
        HashMap<String, Object> extras
    ) {
        Stopwatch stopwatch = new Stopwatch().start();
        NoiseSpec spec = context.spec;
        DataProfile profile = context.profile;
        NoiseReport report = context.report;

        String[] selectedColumns = spec.filteredColumns;
        Preconditions.checkArgument(
            selectedColumns.length == 1,
            "Outlier currently only supports one column (univariable)."
        );

        String selectedColumn = selectedColumns[0];
        DataType type = profile.getType(selectedColumn);
        int columnIndex = profile.getColumnIndex(selectedColumn);

        Preconditions.checkArgument(
            type == DataType.Numerical,
            "Outlier only applies to numerical data."
        );

        double perc = spec.percentage;
        int len = (int)Math.floor(perc * profile.getLength());

        Double std = profile.getStandardDeviationOn(selectedColumn);
        Double mean = profile.getMean(selectedColumn);

        Double lmax = mean + 2 * std; // 95 % in the bell
        Double rmax = mean + 5 * std; // (extra 3 * std)

        ModelBase randomModal = ModelFactory.createRandomModel();
        Random random = new Random();
        int index;
        for (int i = 0; i < len; i ++) {
            // TODO: should we ignore the point already in the outlier region
            // how to deal with small deviation data?
            index = randomModal.nextIndexWithoutReplacement(0, profile.getLength(), true);
            double u = Math.random() * (rmax - lmax) + lmax;
            double v = random.nextGaussian() + u;
            Pair<Integer, Integer> record = new Pair<>(index, columnIndex);
            tracer.verbose(String.format("[%d, %d] <- %f", index, columnIndex, v));
            report.logChange(record, profile.getCell(index, columnIndex), Double.toString(v));

            while (!profile.set(record, Double.toString(v))) {
                index = randomModal.nextIndexWithoutReplacement(0, profile.getLength(), false);
                record = new Pair<>(index, columnIndex);
            }
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        stopwatch.stop();
    }
}
