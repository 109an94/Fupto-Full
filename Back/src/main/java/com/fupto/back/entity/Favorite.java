package com.fupto.back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "favorite")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "mapping_id", nullable = false)
    private Long mappingId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;

    @Column(name = "state")
    private Boolean state;

    @ColumnDefault("current_timestamp()")
    @Column(name = "create_date", insertable = false, updatable = false)
    private Instant createDate;

    @ColumnDefault("current_timestamp()")
    @Column(name = "update_date", insertable = false)
    private Instant updateDate;

    @Column(name = "alert_price", nullable = true)
    private Integer alertPrice;
}