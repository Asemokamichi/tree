package tree;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jdk.jfr.Category;
import tree.entity.Tree;

import java.util.List;
import java.util.Scanner;

public class FindTree {
    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        // Введите название категории: Процессоры

        // Процессоры
        // - Intel
        // - AMD

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        try {
            manager.getTransaction().begin();

            System.out.print("Введите название категории: ");
            String categoryName = scan.nextLine();

            Tree tree1 = manager.createQuery("select t from Tree t where t.name = ?1",
                            Tree.class)
                    .setParameter(1, categoryName)
                    .getSingleResult();


            List<Tree> treeList = manager.createQuery(
                            "select t from Tree t where t.leftKey>=?1 and t.rightKey<=?2 order by t.leftKey",
                            Tree.class)
                    .setParameter(1, tree1.getLeftKey())
                    .setParameter(2, tree1.getRightKey())
                    .getResultList();

            for (Tree tree : treeList) {
                System.out.println("- ".repeat(tree.getLevel() - tree1.getLevel()) + tree.getName());
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
