package tree;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import tree.entity.Tree;

import java.util.Scanner;

public class TreeApplication {
    private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
//         - Создать категорию [1]
//         - Удалить категорию [2]
//         - Переместить категорию [3]
        // Выберите действие: ___
        while (true){
            System.out.print("- Создать категорию [1]\n" +
                    "- Удалить категорию [2]\n" +
                    "- Переместить категорию [3]\n" +
                    "- Exit [4]\n" +
                    "Выберите действие: ");

            int action = Integer.parseInt(scan.nextLine());
            switch (action) {
                case 1 -> create();
                case 2 -> delete();
                case 3 -> update();
                case 4 -> {
                    System.out.println("Выход...");
                    return;
                }
            }
        }
    }

    private static void create() {
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
            System.out.println("Было поймано исключение " + e);
        } finally {
            manager.close();
        }
    }

    private static void update() {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();

            int treeID, treeParentID;
            Tree tree = null, treeParent = null;

            while (true) {
                try {
                    System.out.print("Введите id перемещаемой категории: ");
                    treeID = Integer.parseInt(scan.nextLine());
                    tree = manager.find(Tree.class, treeID);

                    System.out.print("Введите id новой родительской категории: ");
                    treeParentID = Integer.parseInt(scan.nextLine());
                    ;
                    if (treeParentID == 0) break;
                    treeParent = manager.find(Tree.class, treeParentID);
                    if (tree.getLeftKey() <= treeParent.getLeftKey() && treeParent.getLeftKey() <= tree.getRightKey())
                        throw new Exception();
                    break;
                } catch (Exception e) {
                    System.out.println("Некорректные данные. Повторите запрос!" + e);
                }
            }
            manager.createQuery(
                            "update Tree t " +
                                    "set t.leftKey = -t.leftKey, " +
                                    "t.rightKey = -t.rightKey " +
                                    "where t.leftKey>=?1 and t.rightKey<=?2"
                    ).setParameter(1, tree.getLeftKey())
                    .setParameter(2, tree.getRightKey())
                    .executeUpdate();

            int count = tree.getRightKey() - tree.getLeftKey() + 1;

            manager.createQuery(
                            "update Tree t " +
                                    "set t.leftKey = t.leftKey - ?1" +
                                    "where t.leftKey > ?2")
                    .setParameter(1, count)
                    .setParameter(2, tree.getRightKey())
                    .executeUpdate();


            manager.createQuery(
                            "update Tree t " +
                                    "set t.rightKey = t.rightKey - ?1" +
                                    "where t.rightKey >= ?2")
                    .setParameter(1, count)
                    .setParameter(2, tree.getRightKey())
                    .executeUpdate();

            if (treeParentID == 0) {
                Integer id = manager.createQuery(
                        "select max(t.rightKey) from Tree t",
                        Integer.class
                ).getSingleResult();
                manager.createQuery(
                                "update Tree t " +
                                        "set t.leftKey = -t.leftKey + ?1, " +
                                        "t.rightKey = -t.rightKey + ?1," +
                                        "t.level = t.level + ?2 " +
                                        "where t.leftKey < 0")
                        .setParameter(1, id + count - tree.getRightKey())
                        .setParameter(2, -tree.getLevel())
                        .executeUpdate();

                manager.getTransaction().commit();
            }
        } catch (Exception e) {
            manager.getTransaction().rollback();
            System.out.println("Было поймано исключение " + e);
        } finally {
            manager.close();
        }
    }

    private static void delete() {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();

            System.out.print("Введите id категории: ");
            int ID = Integer.parseInt(scan.nextLine());

            Tree perentTree = manager.find(Tree.class, ID);

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
            System.out.println("Было поймано исключение " + e);
        } finally {
            manager.close();
        }
    }
}
