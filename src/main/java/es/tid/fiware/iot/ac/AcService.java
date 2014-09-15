package es.tid.fiware.iot.ac;

import es.tid.fiware.iot.ac.dao.PolicyDao;
import es.tid.fiware.iot.ac.dao.PolicyDaoInMemory;
import es.tid.fiware.iot.ac.pap.PoliciesEndpoint;
import es.tid.fiware.iot.ac.pdp.PdpEndpoint;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AcService extends io.dropwizard.Application<AcConfig> {

    public static void main(String[] args) throws Exception {
        new AcService().run(args);
    }

    @Override
    public void initialize(Bootstrap<AcConfig> bootstrap) {
        /*bootstrap.setName("Droptter");
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(migrations);*/
    }

    @Override
    public void run(AcConfig configuration,
            Environment environment)
            throws Exception {

        PolicyDao dao = new PolicyDaoInMemory();
        environment.jersey().register(new PoliciesEndpoint(dao));
        environment.jersey().register(new PdpEndpoint(dao));

    }
}
