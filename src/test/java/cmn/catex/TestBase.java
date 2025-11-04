/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex;

import htsjdk.io.HtsPath;
import htsjdk.io.IOPath;

public class TestBase {
    final static String GENOMICS_TEST_FILES = "src/test/resources/cmn/genomics/";

    public static final IOPath TEST_BAM = new HtsPath(GENOMICS_TEST_FILES + "CEUTrio.HiSeq.WGS.b37.NA12878.20.21.bam");
    public static final IOPath TEST_BAM_INDEX = new HtsPath(GENOMICS_TEST_FILES + "CEUTrio.HiSeq.WGS.b37.NA12878.20.21.bam.bai");
    public static final IOPath TEST_GVCF = new HtsPath(GENOMICS_TEST_FILES + "threeSamples.MT.g.vcf");
}
