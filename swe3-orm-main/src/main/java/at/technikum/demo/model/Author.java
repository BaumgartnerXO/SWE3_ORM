package at.technikum.demo.model;
import at.technikum.orm.annotations.Column;
import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "authors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    @Id
    private Integer a_id;

    @Column("fname")
    private String firstName;
    @Column("lname")
    private String lastName;
}
