package com.moducation.library.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.sql.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private LibraryUser libraryUser;

    @Column(nullable = false)
    private Float amount;

    @ManyToOne
    @JoinColumn(name = "withdrawal_id", nullable = false)
    private BookWithdrawal withdrawal;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateIssued;

    @PrePersist
    protected void onCreate() {
        this.dateIssued = new Date(System.currentTimeMillis());
    }
}