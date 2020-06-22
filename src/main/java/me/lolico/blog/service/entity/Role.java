package me.lolico.blog.service.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Lolico Li
 */
@Data
@Entity
@Table(name = "blog_roles")
public class Role implements Serializable {

    public static final String PREFIX = "ROLE_";

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "role_name", unique = true)
    private String name;
}
