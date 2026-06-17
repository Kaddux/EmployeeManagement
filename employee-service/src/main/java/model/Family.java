package model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID family_id;

    @Column
    private String number_of_members;

    @Column
    private UUID employee_id;

    @Column
    private String father_name;

    @Column
    private String mother_name;
}
