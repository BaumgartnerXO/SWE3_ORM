package at.technikum.domain;

import at.technikum.orm.annotations.Column;
import at.technikum.orm.annotations.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractEntity {

    @Id
    private Integer id;
    @Column
    private String field;
}
