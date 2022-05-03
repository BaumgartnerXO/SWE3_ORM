package at.technikum.domain;

import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.Id;
import at.technikum.orm.annotations.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity(tableName = "table")
@Data
@AllArgsConstructor
public class MediumEntity {
    @Id
    private Integer id;
    private String value;
    private boolean flag;
    @OneToMany
    private SimpleEntity oneToMany;


}
