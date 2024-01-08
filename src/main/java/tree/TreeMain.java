package tree;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import tree.entity.Tree;

import java.util.List;

public class TreeMain {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        try {
            manager.getTransaction().begin();

            TypedQuery<Tree> typedQuery = manager.createQuery(
                    "select t from Tree t",
                    Tree.class);

            List<Tree> treeList = typedQuery.getResultList();
            for(Tree tree : treeList){
                System.out.println("- ".repeat(tree.getLevel() + 1) + tree.getName());
            }
            manager.getTransaction().commit();
        }catch (Exception e){
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
