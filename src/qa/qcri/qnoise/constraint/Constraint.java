/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.NoiseReport;

/**
 * Constraint class for inconsistency.
 */
public abstract class Constraint {
    public abstract Constraint parse(String text);

    public abstract boolean isValid(DataProfile profile, int index);

    public abstract int messIt(DataProfile profile, int index, double distance, NoiseReport report);
}
