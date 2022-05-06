package at.technikum.demo.model;

import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Id;
import at.technikum.orm.annotations.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(tableName = "borrows")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Borrow {
    @Id
    private Integer borrow_id;

    private LocalDate takenDate;

    private LocalDate broughtDate;


    @OneToMany(columnName = "id_fk")
    private Teacher person;

    @OneToMany(columnName = "b_id_fk")
    private Book book;


}
