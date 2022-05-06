package at.technikum.demo.model;
import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "types")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Type {
    @Id
    private Integer t_id;

    private String name;

}
