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
 * The Role of a {@link User}.
 */
@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
public class Role implements Comparable<Role> {

    public static final String ROLE_SEARCH = "SEARCH";

    public static final String ROLE_ADMIN = "ADMIN";

    public static final String ROLE_REPORTER = "REPORTER";

    public static final String ROLE_APPROVER = "APPROVER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Override
    public int compareTo(Role other) {
        return ObjectUtils.compare(this.code, other.code);
    }

}
