package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {
    @PersistenceContext
    private EntityManager entityManager;

    public CategoryEntity getCategoryById(String categoryId) {
        try {
            return entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class)
                    .setParameter("categoryId", categoryId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CategoryEntity getCategoryByUuid(String uuid) {
        try {
            CategoryEntity categoryEntity = entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class).setParameter("categoryId", uuid).getSingleResult();
            return categoryEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<CategoryItemEntity> getItemByCategory(CategoryEntity categoryEntity) {
        try {
            List<CategoryItemEntity> categoryItemEntities = entityManager.createNamedQuery("getItemsByCategory", CategoryItemEntity.class).setParameter("category", categoryEntity).getResultList();
            return categoryItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
