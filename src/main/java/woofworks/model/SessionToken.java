package woofworks.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Tim on 2016-11-12.
 */
@Entity
@SelectBeforeUpdate
@DynamicInsert
@DynamicUpdate
@SequenceGenerator(name = "SEQ_STORE", sequenceName = "sessionTokenIdSeq", allocationSize = 1)
public class SessionToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_STORE")
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR2(4000)")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User sessionUser;

    @Column(nullable = false)
    private LocalDateTime expires;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

}
