package at.technikum.orm.cache;

import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class TestEntity2 {
    @Id
    private Integer id;

}
