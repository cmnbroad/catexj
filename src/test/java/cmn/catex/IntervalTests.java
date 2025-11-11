/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex;

import cmn.catex.cat.FiniteCategory;
import cmn.catex.genomics.intervals.GenomicIntervalTestUtils;

import cmn.catex.utils.GraphUtils;
import htsjdk.beta.plugin.IOUtils;
import htsjdk.io.IOPath;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalList;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IntervalTests extends TestBase {

    @Test
    private void testContainmentFromBAM() {
        // has 3 reads with 2 contained intervals each
        // Element 20:1-100	+	. has multiple outgoing morphisms:
        //  -> 20:2-99	+	.
        //  -> 20:101-200	+	.
        // Element 20:2-99	+	. has multiple outgoing morphisms:
        //  -> 20:3-98	+	.
        //  -> 20:101-200	+	.
        // Element 20:3-98	+	. has multiple outgoing morphisms:
        //  -> 20:4-97	+	.
        //  -> 20:101-200	+	.
        final SAMSequenceDictionary samDict =
                GenomicIntervalTestUtils.intervalListFromBAM(TEST_BAM)
                        .getHeader()
                        .getSequenceDictionary();
        final IntervalList intervalList = new IntervalList(samDict);
        intervalList.add(new Interval("20", 1, 100));
        intervalList.add(new Interval("20", 2, 99));
        intervalList.add(new Interval("20", 3, 98));
        intervalList.add(new Interval("20", 4, 97));
        intervalList.add(new Interval("20", 101, 200));

        final FiniteCategory<IntervalList, Interval> finiteCategory =
                GenomicIntervalTestUtils.toContainsCategory(intervalList);
        final IOPath tempPDFPath = getTempPDFPathForCategory(finiteCategory);
        // assert nothing for now
        Assert.assertTrue(true);
    }

    @Test
    private void testContainmentFromFeatureFile() {
        // has 2 features with 2 contained intervals each
        // Element MT:16179-16181	+	interval-10350 has multiple outgoing morphisms:
        //  -> MT:16180-16180	+	interval-10351
        //  -> MT:16183-16183	+	interval-10353
        // Element MT:16263-16264	+	interval-10395 has multiple outgoing morphisms:
        //  -> MT:16264-16264	+	interval-10396
        //  -> MT:16265-16265	+	interval-10397
        final IntervalList intervalList = GenomicIntervalTestUtils.intervalListFromGVCF(TEST_GVCF);
        final FiniteCategory<IntervalList, Interval> finiteCategory =
                GenomicIntervalTestUtils.toContainsCategory(intervalList);
        final IOPath tempPDFPath = getTempPDFPathForCategory(finiteCategory);
        // assert nothing for now
        Assert.assertTrue(true);
    }

    @Test
    private void testContainmentFromIntervalList() {
        // Element chr5:10000-10000	+	one_base_feature has multiple outgoing morphisms:
        //  -> chr5:10001-10000	+	zero_base_feature
        //  -> chr5:1000000-1000000	+	target_at_chrom_end
        final IntervalList intervalList = GenomicIntervalTestUtils.intervalListFromPicardIntervalList(TEST_INTERVAL_LIST);
        final FiniteCategory<IntervalList, Interval> finiteCategory =
                GenomicIntervalTestUtils.toContainsCategory(intervalList);
        final IOPath tempPDFPath = getTempPDFPathForCategory(finiteCategory);
        // assert nothing for now
        Assert.assertTrue(true);
    }

    @Test
    private void testOverlapsFromBAM() {
        // Element 20:1-100	+	. has multiple outgoing morphisms:
        //  -> 20:2-99	+	.
        //  -> 20:101-200	+	.
        // Element 20:2-99	+	. has multiple outgoing morphisms:
        //  -> 20:3-98	+	.
        //  -> 20:101-200	+	.
        // Element 20:3-98	+	. has multiple outgoing morphisms:
        //  -> 20:4-97	+	.
        //  -> 20:101-200	+	.
        final SAMSequenceDictionary samDict = GenomicIntervalTestUtils.intervalListFromBAM(TEST_BAM)
                .getHeader()
                .getSequenceDictionary();
        final IntervalList intervalList = new IntervalList(samDict);
        intervalList.add(new Interval("20", 1, 100));
        intervalList.add(new Interval("20", 2, 99));
        intervalList.add(new Interval("20", 3, 98));
        intervalList.add(new Interval("20", 4, 97));
        intervalList.add(new Interval("20", 101, 200));

        final FiniteCategory<IntervalList, Interval> finiteCategory =
                GenomicIntervalTestUtils.toOverlapsCategory(intervalList);
        final IOPath tempPDFPath = getTempPDFPathForCategory(finiteCategory);
        // assert nothing for now
        Assert.assertTrue(true);
    }

    @Test
    private void testOverlapsFromFeatureFile() {
        // Element MT:16179-16181	+	interval-10350 has multiple outgoing morphisms:
        //  -> MT:16180-16180	+	interval-10351
        //  -> MT:16183-16183	+	interval-10353
        //  Element MT:16263-16264	+	interval-10395 has multiple outgoing morphisms:
        //  -> MT:16264-16264	+	interval-10396
        //  -> MT:16265-16265	+	interval-10397
        final IntervalList intervalList = GenomicIntervalTestUtils.intervalListFromGVCF(TEST_GVCF);
        final FiniteCategory<IntervalList, Interval> finiteCategory =
                GenomicIntervalTestUtils.toOverlapsCategory(intervalList);
        final IOPath tempPDFPath = getTempPDFPathForCategory(finiteCategory);
        // assert nothing for now
        Assert.assertTrue(true);
    }

    private IOPath getTempPDFPathForCategory(final FiniteCategory<IntervalList, Interval> category) {
        final IOPath tempDotPath = IOUtils.createTempPath("pdfCategoryTestDOT", ".dot");
        category.writeAsGraph(tempDotPath);
        final IOPath tempPDFPath = IOUtils.createTempPath("pdfCategoryTestPDF", ".pdf");
        GraphUtils.writeDOTToExternal(tempDotPath, tempPDFPath, GraphUtils.GraphFileType.PDF);
        return tempPDFPath;
    }

    private static <S, E> void reportMultiMorphsims(
            FiniteCategory<S, E> finiteCategory,
            final String header) {
        System.out.println("---- " + header + " ----");
        finiteCategory.getMorphismMap().entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry -> {
                    System.out.println("Element " + entry.getKey() + " has multiple outgoing morphisms:");
                    entry.getValue().forEach(value -> System.out.println("  -> " + value));
                });
    }
}
