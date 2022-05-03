package at.technikum.domain;

import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity(tableName = "table")
@Data
@AllArgsConstructor
public class SimpleEntity {
    @Id
    private Integer id;

}
