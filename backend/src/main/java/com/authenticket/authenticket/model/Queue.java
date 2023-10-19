package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "queue")
@IdClass(EventUserId.class)
public class Queue {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;

    @Column(name = "can_purchase")
    private Boolean canPurchase;

    @Column(name = "time")
    private LocalDateTime time;

    @Override
    public int hashCode() {
        return Objects.hash(event, user);
    }
}
