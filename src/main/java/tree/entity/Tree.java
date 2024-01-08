package tree.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tree")
public class Tree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "left_key")
    private int leftKey;
    @Column(name = "right_key")
    private int rightKey;
    private int level;
    private String name;

    @Override
    public String toString() {
        return "id=" + id +
                ", leftKey=" + leftKey +
                ", rightKey=" + rightKey +
                ", level=" + level +
                ", name='" + name;
    }

    public long getId() {
        return id;
    }

    public int getLeftKey() {
        return leftKey;
    }

    public int getRightKey() {
        return rightKey;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLeftKey(int leftKey) {
        this.leftKey = leftKey;
    }

    public void setRightKey(int rightKey) {
        this.rightKey = rightKey;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }
}
