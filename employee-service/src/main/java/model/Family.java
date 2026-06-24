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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column
    private String father_name;

    @Column
    private String mother_name;
}
