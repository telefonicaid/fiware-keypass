package es.tid.fiware.iot.ac;

import es.tid.fiware.iot.ac.dao.PolicyDAOHibernate;
import es.tid.fiware.iot.ac.dao.PolicyDao;
import es.tid.fiware.iot.ac.model.Policy;
import es.tid.fiware.iot.ac.pap.PoliciesEndpoint;
import es.tid.fiware.iot.ac.pap.SubjectEndpoint;
import es.tid.fiware.iot.ac.pap.TenantEndpoint;
import es.tid.fiware.iot.ac.pdp.PdpEndpoint;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AcService extends io.dropwizard.Application<AcConfig> {

    private final HibernateBundle<AcConfig> hibernate = new HibernateBundle<AcConfig>(Policy.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(AcConfig configuration) {
            return configuration.getDataSourceFactory();
        }
    };
    
    public static void main(String[] args) throws Exception {
        new AcService().run(args);
    }

    @Override
    public void initialize(Bootstrap<AcConfig> bootstrap) {
        bootstrap.addBundle(hibernate);     
        bootstrap.addBundle(new MigrationsBundle<AcConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AcConfig configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(AcConfig configuration,
            Environment environment)
            throws Exception {

        PolicyDao dao = new PolicyDAOHibernate(hibernate.getSessionFactory());
        environment.jersey().register(new TenantEndpoint(dao));
        environment.jersey().register(new SubjectEndpoint(dao));
        environment.jersey().register(new PoliciesEndpoint(dao));
        environment.jersey().register(new PdpEndpoint(dao));

    }
}
