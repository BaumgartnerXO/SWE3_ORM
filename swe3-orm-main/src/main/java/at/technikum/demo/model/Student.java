package at.technikum.demo.model;

import at.technikum.orm.annotations.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity(tableName = "students")
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class Student extends Person {

    private String GPA;


    public Student(Integer id, String lastName, String firstName, LocalDate enterDate, LocalDate leaveDate, Gender gender, String GPA) {
        super(id, lastName, firstName, enterDate, leaveDate, gender);
        this.GPA = GPA;
    }
}
