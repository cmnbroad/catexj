package cmn.catex;

import cmn.catex.cat.FiniteCategory;
import cmn.catex.cat.FiniteMorphisms;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FiniteCategoryTest {

    @Test
    private void testNoArgConstructor() {
        final FiniteCategory<Object, String> finCat = new FiniteCategory<>(null);
        Assert.assertNull(finCat.getSource());

        finCat.addElement("A");
        finCat.addElement("B");
        finCat.addMorphism("A", "B");

        final Set<String> elements = finCat.getElements();
        Assert.assertTrue(elements.contains("A"));
        Assert.assertTrue(elements.contains("B"));

        final Map<String, List<String>> morphismMap = finCat.getMorphismMap();
        Assert.assertTrue(morphismMap.containsKey("A"));
        Assert.assertEquals(Arrays.asList("B"), morphismMap.get("A"));
    }

    @Test
    public void testSourceConstructor() {
        final Object sourceObj = new Object();
        final FiniteCategory<Object, String> finCat = new FiniteCategory<>(sourceObj);
        Assert.assertEquals(finCat.getSource(), sourceObj);
    }

    // FiniteCategory(final S source, final List<E> elements, final FiniteMorphisms<E> morphisms)
    @Test
    private void testAllArgsConstructor() {
        final Object sourceObj = new Object();
        final List<String> elements = Arrays.asList("X", "Y", "Z");
        final FiniteCategory<Object, String> finCat =
                new FiniteCategory<>(sourceObj, elements, new FiniteMorphisms<>() {
                    {
                        add("X", "Y");
                        add("Y", "Z");
                    }
                });

        Assert.assertEquals(finCat.getSource(), sourceObj);
        Assert.assertEquals(finCat.getElements(), Set.of("X", "Y", "Z"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    private void testAllArgsConstructorRejectsUnknownElementMorphisms() {
        final Object sourceObj = new Object();
        final List<String> elements = Arrays.asList("X", "Y", "Z");
        new FiniteCategory<>(sourceObj, elements, new FiniteMorphisms<>() {
                {
                    add("X", "Y");
                    add("Y", "A"); // 'A' not in elements list
                }
            });
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    private void testRejectMorphismsForUnknownElements() throws IllegalArgumentException {
        // adding a morphism without prior addElement calls
        final FiniteCategory<Object, String> cat = new FiniteCategory<>();
        cat.addMorphism("X", "Y");
    }

    @Test
    private void testConstructorCreatesDeepListsAndIndependentCopy() {
        final FiniteCategory<Object, String> finCat = new FiniteCategory<>();
        finCat.addElements("p", "q", "r", "s");
        finCat.addMorphism("p", "q");
        finCat.addMorphism("r", "s");

        final FiniteCategory<Object, String> catCopy =
                new FiniteCategory<>(
                        null,
                        finCat.getElements().stream().toList(),
                        new FiniteMorphisms<>(finCat.getMorphismMap()));

        // equal content
        Assert.assertEquals(finCat, catCopy);
        Assert.assertEquals(finCat.hashCode(), catCopy.hashCode());

        // mutate original's internal list and verify copy is unaffected
        finCat.getMorphismMap().get("p").add("z");
        Assert.assertTrue(finCat.getMorphismMap().get("p").contains("z"));
        Assert.assertFalse(catCopy.getMorphismMap().get("p").contains("z"));

        // adding new mapping to original does not add to copy
        finCat.addElements("t", "u");
        finCat.addMorphism("t", "u");
        Assert.assertFalse(catCopy.getMorphismMap().containsKey("t"));
    }

    @Test
    private void testEqualsAndHashCodeContract() {
        final FiniteCategory<Object, String> a = new FiniteCategory<>();
        final FiniteCategory<Object, String> b = new FiniteCategory<>();
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());

        a.addElements("m", "n");
        a.addMorphism("m", "n");
        Assert.assertNotEquals(a, b);

        b.addElements("m", "n");
        b.addMorphism("m", "n");
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }
}