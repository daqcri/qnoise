/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

public enum GranularityType {
    Column,
    Row,
    Cell;

    public static GranularityType fromString(String granularity) {
        if (granularity.equalsIgnoreCase("row")) {
            return GranularityType.Row;
        }

        if (granularity.equalsIgnoreCase("cell")) {
            return GranularityType.Cell;
        }

        if (granularity.equalsIgnoreCase("column")) {
            return GranularityType.Column;
        }

        throw new IllegalArgumentException("Unknown granularity string " + granularity);
    }
}