package com.fupto.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "tel", length = 10)
    private String tel;

    @Column(name = "email", nullable = false, length = 256)
    private String email;

    @ColumnDefault("current_timestamp()")
    @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @ColumnDefault("current_timestamp()")
    @Column(name = "update_date", nullable = false)
    private Instant updateDate;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

}