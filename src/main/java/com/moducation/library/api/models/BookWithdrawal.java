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
public class BookWithdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_activity_id", nullable = false)
    private BookActivityHistory bookActivity;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expectedReturnDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private LibraryUser libraryUser;

    @PrePersist
    protected void onCreate() {
        this.expectedReturnDate = new Date(System.currentTimeMillis());
    }
}