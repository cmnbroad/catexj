/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex;

import cmn.catex.cat.FiniteCategory;
import cmn.catex.genomics.intervals.GenomicIntervalTestUtils;

import cmn.catex.utils.GraphUtils;
import htsjdk.beta.plugin.IOUtils;
import htsjdk.io.HtsPath;
import htsjdk.io.IOPath;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalList;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Files;

public class IntervalTests extends TestBase {

    //test files
    private static final IOPath GATK_CLONE = new HtsPath("../hellbender/");

    @BeforeTest
    private void setup() {
        if (Files.notExists(GATK_CLONE.toPath())) {
            throw new IllegalStateException(
                    "Genomics test files not found - clone https://github.com/broadinstitute/gatk " +
                            "as a peer to the catexj repository.");
        }
    }

    @Test
    private void testContainmentFromBAM() {
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
        //note: this test file has no actual contained intervals
        final IntervalList intervalList = GenomicIntervalTestUtils.intervalListFromGVCF(TEST_GVCF);
        final FiniteCategory<IntervalList, Interval> finiteCategory =
                GenomicIntervalTestUtils.toContainsCategory(intervalList);
        final IOPath tempPDFPath = getTempPDFPathForCategory(finiteCategory);
        // assert nothing for now
        Assert.assertTrue(true);
    }

    @Test
    private void testContainmentFromIntervalList() {
        // category to graph
        final IntervalList intervalList = GenomicIntervalTestUtils.intervalListFromPicardIntervalList(
                new HtsPath(GENOMICS_TEST_FILES + "exome_calling_regions.v1.interval_list"));
        final FiniteCategory<IntervalList, Interval> finiteCategory =
                GenomicIntervalTestUtils.toContainsCategory(intervalList);
        final IOPath tempPDFPath = getTempPDFPathForCategory(finiteCategory);
        // assert nothing for now
        Assert.assertTrue(true);
    }

    @Test
    private void testOverlapsFromBAM() {
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

    @DataProvider(name = "overlapFeatureFiles")
    public Object[][] getFeatureFiles() {
        return new Object[][] {
                // test file(s) with overlaps
                { TEST_GVCF },
                { new HtsPath(GATK_CLONE.getRawInputString() + "src/test/resources/large/Mills_and_1000G_gold_standard.indels.b37.sites.chr20.vcf") },
        };
    }

    @Test(dataProvider = "overlapFeatureFiles")
    private void testOverlapsFromFeatureFile(final IOPath filePath) {
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

}
