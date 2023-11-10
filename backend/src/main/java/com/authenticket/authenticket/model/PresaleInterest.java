package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Represents the user's interest in a presale event, linking a user to an event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "presale_interest")
@IdClass(EventUserId.class)
public class PresaleInterest {
    /**
     * The user interested in the presale.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    /**
     * The event associated with the presale interest.
     */
    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;

    /**
     * Indicates whether the user is selected for the presale.
     */
    @Column(name = "is_selected")
    private Boolean isSelected;

    /**
     * Indicates whether an email has been sent to the user regarding this presale interest.
     */
    @Column(name = "emailed")
    private Boolean emailed;

    /**
     * Calculates the hash code of the presale interest based on the associated event and user.
     *
     * @return The hash code for the presale interest.
     */
    @Override
    public int hashCode() {
        return Objects.hash(event, user);
    }
}
