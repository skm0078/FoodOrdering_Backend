package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * create address in database.
     *
     * @param addressEntity : the AddressEntity body
     * @return Address details
     */
    public AddressEntity createAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    /**
     * to all addresses posted by a user
     *
     * @param
     * @return list of addresses
     */
    public List<AddressEntity> getAllAddress() {
        return entityManager
                .createNamedQuery("allAddress", AddressEntity.class)
                .getResultList();
    }

    /**
     * Fetch Question by question Uuid
     *
     * @param addressUuid
     * @return address by Uuid
     */
    public AddressEntity getAddressByUuid(String addressUuid) {
        try {
            return entityManager
                    .createNamedQuery("addressByUuid", AddressEntity.class)
                    .setParameter("Uuid", addressUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * delete a address from Database
     *
     * @param addressEntity
     */
    public void deleteAddress(AddressEntity addressEntity) {

        entityManager.remove(addressEntity);
    }

}
