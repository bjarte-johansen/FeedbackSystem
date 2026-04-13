package root.includes.quicktests.repofun;

import root.interfaces.HasId;

import java.time.Instant;

public class Fantasy implements HasId {
    private Long id;
    private String text;
    private Integer score;
    private String email;
    private Integer age;
    private Instant createdAt;

    public Fantasy() {
    }

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String name) { this.text = name; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString(){
        return "Fantasy" + JsonStringifier.stringify(this);
    }
}
