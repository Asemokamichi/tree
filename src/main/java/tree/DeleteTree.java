package tree;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import tree.entity.Tree;

import java.util.HashSet;
import java.util.Scanner;

public class DeleteTree {
    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();

            System.out.print("Введите id категории: ");
            int ID = Integer.parseInt(scan.nextLine());

            Tree perentTree = manager.find(Tree.class, ID);
            System.out.println(perentTree.getRightKey());

            manager.createQuery(
                            "delete Tree t where t.leftKey >= ?1 and t.rightKey <= ?2"
                    ).setParameter(1, perentTree.getLeftKey())
                    .setParameter(2, perentTree.getRightKey())
                    .executeUpdate();

            manager.createQuery(
                            "update Tree t " +
                                    "set t.leftKey = t.leftKey - ?1" +
                                    "where t.leftKey > ?2")
                    .setParameter(1, perentTree.getRightKey() - perentTree.getLeftKey() + 1)
                    .setParameter(2, perentTree.getRightKey())
                    .executeUpdate();

            manager.createQuery(
                            "update Tree t " +
                                    "set t.rightKey = t.rightKey - ?1" +
                                    "where t.rightKey > ?2")
                    .setParameter(1, perentTree.getRightKey() - perentTree.getLeftKey() + 1)
                    .setParameter(2, perentTree.getRightKey())
                    .executeUpdate();


            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
