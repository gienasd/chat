package pl.pawelprzystarz.chat.models;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "log")
public class LogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String sender;
    private String message;
    private LocalDateTime date;

    public LogModel(){

    }
}
