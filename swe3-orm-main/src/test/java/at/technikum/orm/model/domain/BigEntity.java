package at.technikum.orm.model.domain;

import at.technikum.orm.annotations.Entity;
import at.technikum.orm.annotations.OneToMany;
import at.technikum.orm.annotations.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "table")
@Setter
@Getter
public class BigEntity extends AbstractEntity{

    private String field2;
    private String field3;
    @Transient
    private String transientField;
    @OneToMany
    private SimpleEntity ref1;
    @OneToMany
    private SimpleEntity ref2;
}
