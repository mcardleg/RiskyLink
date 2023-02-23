package ie.tcd.mcardleg.RiskyLinkBackend;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public class RepositoryHolder {

    private Repository repository;
    private RepositoryConnection connection;

    public RepositoryHolder(Repository repository, RepositoryConnection connection) {
        this.repository = repository;
        this.connection = connection;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public RepositoryConnection getConnection() {
        return connection;
    }

    public void setConnection(RepositoryConnection connection) {
        this.connection = connection;
    }
}
