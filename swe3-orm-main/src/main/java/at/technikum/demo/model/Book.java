package at.technikum.demo.model;

import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Id;
import at.technikum.orm.annotations.OneToMany;
import lombok.*;

@Entity(tableName = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {
   @Id
    private Integer b_id;

    private String titel;


    @OneToMany(columnName = "author_fk")
    private Author author;


    @OneToMany(columnName = "type_fk")
    private Type type;


}
