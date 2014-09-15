package es.tid.fiware.iot.ac.playground;

import es.tid.fiware.iot.ac.xacml.InMemoryPolicyFinderModule;
import es.tid.fiware.iot.ac.xacml.PDPFactory;
import org.w3c.dom.Document;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SpikeBalana {

    public static void t1() throws ParsingException {
        Document policy = xml(read("IIIA001Policy.xacml3.xml"));

        PolicyFinder finder= new PolicyFinder();

        PolicyFinderModule testPolicyFinderModule = new InMemoryPolicyFinderModule(Arrays.asList(policy));
        Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
        policyModules.add(testPolicyFinderModule);
        finder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();
        pdpConfig = new PDPConfig(pdpConfig.getAttributeFinder(), finder,
                pdpConfig.getResourceFinder(), false);
        PDP pdp = new PDP(pdpConfig);

        String request =  read("IIIA001Request.xacml3.xml");

        AbstractRequestCtx requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request.replaceAll(">\\s+<", "><"));
        //ResponseCtx responseCtx = pdp.evaluate(requestCtx);
        //System.out.println(responseCtx);
        System.out.println(pdp.evaluate(request));

    }

    public static void t2() {
        PDP pdp = new PDPFactory().build(Arrays.asList(xml(read("IIIA001Policy.xacml3.dlm.xml"))));
        System.out.println(pdp.evaluate(read("IIIA001Request.xacml3.dlm.xml")));
    }

    public static void t3() {
        PDP pdp = new PDPFactory().build(Arrays.asList(xml(read("policy01.xml"))));
        System.out.println(pdp.evaluate(read("policy01_request01.xml")));
    }

    private static String read(String f) {
        return new Scanner(SpikeBalana.class.getResourceAsStream(f)).useDelimiter("\\Z").next();
    }

    private static Document xml(String str) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            DocumentBuilder db = factory.newDocumentBuilder();
            return db.parse(new InputSource(new StringReader(str)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main (String[] args) throws Exception {
        t3();
    }
}
