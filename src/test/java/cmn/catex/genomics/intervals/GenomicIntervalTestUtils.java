/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex.genomics.intervals;

import cmn.catex.cat.FiniteMorphisms;
import cmn.catex.cat.FiniteCategory;
import htsjdk.beta.plugin.reads.ReadsDecoder;
import htsjdk.beta.plugin.registry.HtsDefaultRegistry;
import htsjdk.io.IOPath;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalList;
import htsjdk.variant.vcf.VCFFileReader;
import org.broadinstitute.hellbender.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Utility class for working with IntervalList test objects.
 *
 * TODO: add abuts variation as alternative morphism type
 * TODO: add "overlaps only" and "contains only" as an alternative (keep only overlapping/contained intervals)
 */
public class GenomicIntervalTestUtils {

    public static IntervalList intervalListFromBAM(final IOPath bamFile) {
        try (final ReadsDecoder readsDecoder = HtsDefaultRegistry.getReadsResolver().getReadsDecoder(bamFile)) {
            return new IntervalList(readsDecoder.getHeader());
        }
    }

    public static IntervalList intervalListFromGVCF(final IOPath vcfFile) {
        try (final VCFFileReader reader = new VCFFileReader(vcfFile.toPath(), false)) {
            return reader.toIntervalList();
        }
    }

    public static IntervalList intervalListFromPicardIntervalList(final IOPath intervalListFile) {
        return IntervalList.fromPath(new File(intervalListFile.getRawInputString()).toPath());
    }

    // poset where < => contains
    public static FiniteCategory<IntervalList, Interval> toContainsCategory(final IntervalList intervalList) {
        Utils.validateArg(intervalList != null, "intervalList cannot be null");
        Utils.validateArg(intervalList.size() >= 2, "intervalList must have at least two entries");

        return toCategoryWithProperty(intervalList, Interval::contains);
    }

    // poset where < => overlaps
    public static FiniteCategory<IntervalList, Interval> toOverlapsCategory(final IntervalList intervalList) {
        Utils.validateArg(intervalList != null, "intervalList cannot be null");
        Utils.validateArg(intervalList.size() >= 2, "intervalList must have at least two entries");

        return toCategoryWithProperty(intervalList, Interval::overlaps);
    }

    private static FiniteCategory<IntervalList, Interval> toCategoryWithProperty(
            final IntervalList intervalList,
            final BiFunction<Interval, Interval, Boolean> propertyPredicate)
    {
        final List<Interval> intervals = intervalList.getIntervals();
        final FiniteMorphisms<Interval> morphisms = new FiniteMorphisms<>();
        Interval previousInterval = null;
        for (int i = 0; i < intervals.size() - 1; i++) {
            final Interval sourceInterval = intervals.get(i);
            Interval nextInterval = intervals.get(i + 1);
            morphisms.add(sourceInterval, nextInterval);
            if (previousInterval != null) {
                morphisms.add(previousInterval, nextInterval);
                previousInterval = null;
            }
            final Interval terminalInterval = findTerminalIntervalWithProperty(i + 1, intervals, propertyPredicate);
            if (terminalInterval != null) {
                morphisms.add(sourceInterval, terminalInterval);
            }
        }
        return new FiniteCategory<>(intervalList, intervals, morphisms);
    }

    private static Interval findTerminalIntervalWithProperty(
            final int i,
            final List<Interval> intervals,
            final BiFunction<Interval, Interval, Boolean> propertyPredicate) {
        final Interval sourceInterval = intervals.get(i);
        Interval terminalInterval = null;
        for (int j = i + 1; j < intervals.size(); j++) {
            Interval nextInterval = intervals.get(j);
            if (propertyPredicate.apply(sourceInterval, nextInterval)) {
                terminalInterval = nextInterval;
            } else {
                break;
            }
        }
        return terminalInterval;
    }
}
