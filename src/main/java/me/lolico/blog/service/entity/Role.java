package me.lolico.blog.service.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Lolico Li
 */
@Data
@Entity
@Table(name = "blog_roles")
public class Role {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "role_name", unique = true)
    private String name;
}
