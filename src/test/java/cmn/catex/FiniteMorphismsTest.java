package cmn.catex;

import cmn.catex.cat.FiniteMorphisms;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FiniteMorphismsTest extends TestBase {

    @Test
    private void testAddAndRetrieveMorphisms() {
        final FiniteMorphisms<String> fm = new FiniteMorphisms<>();
        fm.add("a", "b");

        final Map<String, List<String>> map = fm.getMorphismMap();
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertEquals(Arrays.asList("b"), map.get("a"));
    }

    @Test
    private void testPreserveInsertionOrderAndAllowDuplicates() {
        final FiniteMorphisms<String> fm = new FiniteMorphisms<>();
        fm.add("k", "v1");
        fm.add("k", "v2");
        fm.add("k", "v1"); // duplicate allowed

        final List<String> values = fm.getMorphismMap().get("k");
        Assert.assertEquals(3, values.size());
        Assert.assertEquals(Arrays.asList("v1", "v2", "v1"), values);
    }

    @Test
    private void testCopyConstructorCreatesDeepListsAndIndependentCopy() {
        final FiniteMorphisms<String> original = new FiniteMorphisms<>();
        original.add("x", "a");
        original.add("y", "b");

        final FiniteMorphisms<String> copy = new FiniteMorphisms<>(original);

        // equal content
        Assert.assertEquals(original, copy);
        Assert.assertEquals(original.hashCode(), copy.hashCode());

        // mutate original's internal list and verify copy is unaffected
        original.getMorphismMap().get("x").add("c");
        Assert.assertTrue(original.getMorphismMap().get("x").contains("c"));
        Assert.assertFalse(copy.getMorphismMap().get("x").contains("c"));

        // adding new mapping to original does not add to copy
        original.add("z", "d");
        Assert.assertFalse(copy.getMorphismMap().containsKey("z"));
    }

    @Test
    private void testEqualsAndHashCodeContract() {
        final FiniteMorphisms<String> a = new FiniteMorphisms<>();
        final FiniteMorphisms<String> b = new FiniteMorphisms<>();
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());

        a.add("k", "v");
        Assert.assertNotEquals(a, b);

        b.add("k", "v");
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }
}