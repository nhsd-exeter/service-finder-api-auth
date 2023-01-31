package uk.nhs.digital.uec.api.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Region a {@link User} belongs to.
 */
@Entity
@Table(name = "region")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
public class Region implements Comparable<Region> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Override
    public int compareTo(Region other) {
        return ObjectUtils.compare(this.code, other.code);
    }

}
