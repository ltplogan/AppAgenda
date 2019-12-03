/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import entidades.Provincia;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


/**
 *
 * @author Lorenzo
 */
public class AppAgenda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Map<String, String> emfProperties = new HashMap();
        emfProperties.put("javax.persistence.schema-generation.database.action", "create");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU", emfProperties); 
        EntityManager em = emf.createEntityManager();
        
        Query queryProv = em.createNamedQuery("Provincia.findAll");
        List<Provincia> listProv = queryProv.getResultList();
        for(int i = 0;i < listProv.size();i++) {
            Provincia prov = listProv.get(i);
            System.out.println(prov.getNombre());
        }
        
        Query queryProvincias = em.createNamedQuery("Provincia.findByNombre");
        queryProvincias.setParameter("nombre", "Cádiz");
        List<Provincia> listProvincias = queryProvincias.getResultList();
        for(Provincia provincia:listProvincias) {
            System.out.println(provincia.getId() + ":");
            System.out.println(provincia.getNombre());
        }
        
        Provincia provinciaId2 = em.find(Provincia.class,2);
        if(provinciaId2 != null) {
            System.out.print(provinciaId2.getId() + ":");
            System.out.println(provinciaId2.getNombre() + ":");
        } else {
            System.out.println("No hay ninguna provincia con ID=2");
        }
        
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz = queryProvinciaCadiz.getResultList();
        em.getTransaction().begin();
        listProvinciasCadiz.forEach((provinciaCadiz) -> {
            provinciaCadiz.setCodigo("CA");
            em.merge(provinciaCadiz);
        });
        em.getTransaction().commit();
        
        //Para eliminar
        Provincia provinciaId42 = em.find(Provincia.class, 42);
        em.getTransaction().begin();
        if(provinciaId42 != null) {
            em.remove(provinciaId42);
        } else {
            System.out.println("No hay ninguna provincia con ID=42");
        }
        
        //
        Provincia provinciaCadiz = new Provincia(0, "Cádiz");
        Provincia provinciaSevilla = new Provincia();
        provinciaSevilla.setNombre("Sevilla");
        
        /*em.getTransaction().begin();
        em.persist(provinciaCadiz);
        em.persist(provinciaSevilla);
        em.getTransaction().commit();*/
        //
        
        em.close();
        emf.close();
        try {
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch (SQLException ex) {
            
        }
    }
    
}
