package pl.pawelprzystarz.chat.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.pawelprzystarz.chat.models.LogModel;

@Repository
public interface LogRepository extends CrudRepository<LogModel,Integer>{

}
