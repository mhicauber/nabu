package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

import static no.rutebanken.nabu.repository.DbStatusChecker.isPostgresUp;

@Repository
@Transactional
public class JpaProviderRepository implements ProviderRepository, DbStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Collection<Provider> getProviders() {
        return this.entityManager.createQuery("SELECT p FROM Provider p", Provider.class).getResultList();
    }

    @Override
    public Provider getProvider(Long id) {
        return entityManager.find(Provider.class, id);
    }

    @Override
    public boolean isDbUp() {
        return isPostgresUp(entityManager, logger);
    }
}