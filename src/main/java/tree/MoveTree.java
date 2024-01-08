package tree;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import tree.entity.Tree;

import java.util.Scanner;

public class MoveTree {
    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        // Введите id перемещаемой категории: 2.
        // Введите id новой родительской категории: 5.

        // 1) Сделать ключи перемещаемой категории отрицательными.
        // 2) Убрать образовавшийся промежуток.
        // 3) Выделить место в новой родительской категории.
        // 4) Перевести отцательные ключи перемещаемой катгеории в положительные.

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        try {
            manager.getTransaction().begin();
            int treeID, treeParentID;
            Tree tree = null, treeParent=null;

            while (true) {
                try {
                    System.out.print("Введите id перемещаемой категории: ");
                    treeID = Integer.parseInt(scan.nextLine());
                    tree = manager.find(Tree.class, treeID);

                    System.out.print("Введите id новой родительской категории: ");
                    treeParentID = Integer.parseInt(scan.nextLine());;
                    if (treeParentID == 0) break;
                    treeParent = manager.find(Tree.class, treeParentID);
                    if (tree.getLeftKey() <= treeParent.getLeftKey() &&  treeParent.getLeftKey() <= tree.getRightKey()) throw new Exception();
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
                return;
            }

            int rightKey = tree.getRightKey() < treeParent.getLeftKey() ? treeParent.getRightKey() - count : treeParent.getRightKey();

            manager.createQuery(
                            "update Tree t " +
                                    "set t.leftKey = t.leftKey + ?1" +
                                    "where t.leftKey > ?2")
                    .setParameter(1, count)
                    .setParameter(2, rightKey)
                    .executeUpdate();

            manager.createQuery(
                            "update Tree t " +
                                    "set t.rightKey = t.rightKey + ?1" +
                                    "where t.rightKey >= ?2")
                    .setParameter(1, count)
                    .setParameter(2, rightKey)
                    .executeUpdate();

            rightKey += count;
//
//
//            // -2 -7 -> 2 9 -> 3 8
//            // -3 -4           4 5
//            // -5 -6           6 7
//
//            // 0 - (-2) + (19 - 7 - 1) = 3
//            // 0 - (-7) + (9 - 7 - 1) = 8
//            // 0 - (-3) + (9 - 7 - 1) = 4
//            // 0 - (-4) + (9 - 7 - 1) = 5
//            // 0 - (-5) + (9 - 7 - 1) = 6
//            // 0 - (-6) + (9 - 7 - 1) = 7
//               -2 -7 -> 2 19 -> 19 24
//            // -3 -4           20 21
//            // -5 -6           22 23
//
//            // 0 - (-2) + (9 - 7 - 1) = 3
//            // 0 - (-7) + (9 - 7 - 1) = 8
//            // 0 - (-3) + (9 - 7 - 1) = 4
//            // 0 - (-4) + (9 - 7 - 1) = 5
//            // 0 - (-5) + (9 - 7 - 1) = 6
//            // 0 - (-6) + (9 - 7 - 1) = 7
//
//            // 0 - актуальный левый/правый ключ + (правый ключ нового родителя - правый ключ перемещаемой - 1)
//            System.out.println(treeParent.getLevel() - tree.getLevel());
            manager.createQuery(
                            "update Tree t " +
                                    "set t.leftKey = -t.leftKey + ?1, " +
                                    "t.rightKey = -t.rightKey + ?1," +
                                    "t.level = t.level + ?2 " +
                                    "where t.leftKey < 0")
                    .setParameter(1, rightKey - tree.getRightKey() - 1)
                    .setParameter(2, treeParent.getLevel() - tree.getLevel() + 1)
                    .executeUpdate();


            manager.getTransaction().commit();
        } catch (
                Exception e) {
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
