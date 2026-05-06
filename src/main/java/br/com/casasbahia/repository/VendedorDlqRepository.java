package br.com.casasbahia.repository;

import br.com.casasbahia.dlq.entity.VendedorDlqEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendedorDlqRepository extends MongoRepository<VendedorDlqEntity, String> {
    Optional<VendedorDlqEntity> findByProtocolo(String Protocolo);

}
