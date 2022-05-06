package at.technikum.demo.model;

import at.technikum.orm.annotations.Column;
import at.technikum.orm.annotations.Id;
import at.technikum.orm.annotations.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class Person {

    @Id
    private Integer id;

    private String lastName;

    private String firstName;

    private LocalDate enterDate;

    private LocalDate leaveDate;

    private Gender gender;

    public Person(Integer id, String lastName, String firstName, LocalDate enterDate, LocalDate leaveDate, Gender gender) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.enterDate = enterDate;
        this.leaveDate = leaveDate;
        this.gender = gender;
    }
}
