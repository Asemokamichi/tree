package tree;

import jakarta.persistence.*;
import tree.entity.Tree;

import java.util.Scanner;

public class InsertUpdateTreeTable {
    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();

            System.out.print("Введите id родительской категории: ");
            int ID = Integer.parseInt(scan.nextLine());

            System.out.print("Введите название новой категории: ");
            String name = scan.nextLine();

            Tree tree = new Tree();

            if (ID == 0) {
                Integer id = manager.createQuery(
                        "select max(t.rightKey) from Tree t",
                        Integer.class
                ).getSingleResult();
                tree.setLeftKey(id + 1);
                tree.setRightKey(id + 2);
                tree.setLevel(0);
                tree.setName(name);
                return;
            }


            Tree parentTree = manager.find(Tree.class, ID);
            System.out.println(parentTree.getRightKey());

            manager.createQuery(
                            "update Tree t " +
                                    "set t.leftKey = t.leftKey + 2" +
                                    "where t.leftKey > ?1")
                    .setParameter(1, parentTree.getRightKey())
                    .executeUpdate();

            manager.createQuery(
                            "update Tree t " +
                                    "set t.rightKey = t.rightKey + 2" +
                                    "where t.rightKey >= ?1")
                    .setParameter(1, parentTree.getRightKey())
                    .executeUpdate();
            tree.setLeftKey(parentTree.getRightKey());
            tree.setRightKey(parentTree.getRightKey() + 1);
            tree.setLevel(parentTree.getLevel() + 1);
            tree.setName(name);


            manager.persist(tree);

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
