package at.technikum.demo.model;


import at.technikum.orm.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "invalidEntity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvalidEntity {
    private Integer id;
}
