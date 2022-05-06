package at.technikum.demo.model;

import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity(tableName = "teachers")
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class Teacher extends Person {

    private int salary;

    @Transient
    private boolean isAbsent;


    public Teacher(Integer id, String lastName, String firstName, LocalDate enterDate, LocalDate leaveDate,Gender gender, int salary) {
        super(id, lastName, firstName, enterDate, leaveDate, gender);
        this.salary = salary;

    }


}
