package br.com.casasbahia.repository;


import br.com.casasbahia.domain.Vendedor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VendedorRepository extends MongoRepository<Vendedor, ObjectId> {
    Optional<Vendedor> findByMatricula(String matricula);
    Optional<Vendedor> findByProtocolo(String Protocolo);

}
