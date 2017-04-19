package org.wildfly.discovery;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wildfly.discovery.impl.StaticDiscoveryProvider;
import org.wildfly.discovery.spi.DiscoveryProvider;

import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tests for FilterSpec functionality
 *
 * @author <a href="mailto:rachmato@redhat.com">Richard Achmatowicz</a>
 */
public final class FilterSpecTestCase {

    private static DiscoveryProvider provider = null;
    private static Discovery discovery = null;

    @BeforeClass
    public static void setup() throws Exception {
        // do any setup here which applies to all tests

        List<ServiceURL> list = new ArrayList<ServiceURL>();
        // add a Service URL
        AttributeValuePair clusterPair = new AttributeValuePair("cluster","c");
        ServiceURL cluster = buildSingleAttributeServiceURL(clusterPair);
        list.add(cluster);

        AttributeValuePair modulePair = new AttributeValuePair("module","m");
        ServiceURL module = buildSingleAttributeServiceURL(modulePair);
        list.add(module);

        ServiceURL combo = buildMultiAttributeServiceURL(clusterPair, modulePair);
        list.add(combo);

        provider = new StaticDiscoveryProvider(list);
        discovery = Discovery.create(provider);
    }

    @Before
    public void setupTest() {
        // do any test-sepcific setup here
    }

    @Test
    public void testFilterSpecContents() {
        // specify attribute=*
        FilterSpec attr = FilterSpec.hasAttribute("fred");
        assertEquals(attr.toString(),"(fred=*)");

        // specify attribute=X
        FilterSpec equals = FilterSpec.equal("fred","barney");
        System.out.println("FilterSpec = " + equals.toString());

        // specify all
        FilterSpec all = FilterSpec.all(attr, equals);
        System.out.println("FilterSpec = " + all.toString());

        // specify any
        FilterSpec any = FilterSpec.any(attr, equals);
        System.out.println("FilterSpec = " + any.toString());
    }

    @Test
    public void testDiscoverySingleAttribute() {

        FilterSpec cluster = FilterSpec.equal("cluster","c");

        // call discovery for single attribute
        System.out.println("Calling discover for filterspec " + cluster);
        try (final ServicesQueue servicesQueue = discover(cluster)) {
            ServiceURL serviceURL = servicesQueue.takeService();
            do {
                System.out.println("ServiceURL found = " + serviceURL);
                serviceURL = servicesQueue.takeService();
            } while (serviceURL != null) ;
        } catch (InterruptedException ie) {
            System.out.println("Interrupted ...");
        }
    }

    @Test
    public void testDiscoveryMultipleAttributes() {

        FilterSpec cluster = FilterSpec.equal("cluster","c");
        FilterSpec module = FilterSpec.equal("module","m");
        FilterSpec all = FilterSpec.all(cluster,module);

        // call discovery for single attribute
        System.out.println("Calling discover for filterspec " + all);
        try (final ServicesQueue servicesQueue = discover(all)) {
            ServiceURL serviceURL = servicesQueue.takeService();
            do {
                System.out.println("ServiceURL found = " + serviceURL);
                serviceURL = servicesQueue.takeService();
            } while (serviceURL != null) ;
        } catch (InterruptedException ie) {
            System.out.println("Interrupted ...");
        }
    }


    @After
    public void tearDownTest() {
        // do any test-specific tear down here
    }

    @AfterClass
    public static void tearDown() {
        // do any general tear down here
    }

    private static ServicesQueue discover(FilterSpec filterSpec) {
        ServiceType serviceType = new ServiceType("ejb","jboss", null, null);
        return discovery.discover(serviceType, filterSpec);
    }

    private static class AttributeValuePair {
        String attribute = null;
        String value = null;

        public AttributeValuePair(String attribute, String value) {
            this.attribute = attribute;
            this.value = value;
        }

        public String getAttribute() {
            return attribute;
        }

        public String getValue() {
            return value;
        }
    }

    private static ServiceURL buildSingleAttributeServiceURL(AttributeValuePair pair) throws Exception {

        final ServiceURL.Builder builder = new ServiceURL.Builder();
        // set the locationURI
        builder.setUri(new URI("http://myhost.com"));
        builder.setAbstractType("ejb");
        builder.setAbstractTypeAuthority("jboss");
        // add an attribute
        builder.addAttribute(pair.getAttribute(), AttributeValue.fromString(pair.getValue()));
        return builder.create();
    }
    private static ServiceURL buildMultiAttributeServiceURL(AttributeValuePair ...pairs) throws Exception {

        final ServiceURL.Builder builder = new ServiceURL.Builder();
        // set the locationURI
        builder.setUri(new URI("http://myhost.com"));
        builder.setAbstractType("ejb");
        builder.setAbstractTypeAuthority("jboss");
        // add an attribute
        for (AttributeValuePair pair : pairs) {
            builder.addAttribute(pair.getAttribute(), AttributeValue.fromString(pair.getValue()));
        }
        return builder.create();
    }

}
