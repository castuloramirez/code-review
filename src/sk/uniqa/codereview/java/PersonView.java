package sk.uniqa.codereview.java;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.management.relation.Role;
import javax.persistence.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

@Named
@SessionScoped
public class PersonView {

    @Inject
    PersonService personService;

    @Inject
    private AccountService account_service;

    @Getter
    @Setter
    private List<Person> people;
    
    private String appDescription;
    
    public PersonView() {
        people.clear();
        people = personService.findAll();
        
        Path path = Paths.get("src/main/resources/appDescription.txt");
        try {
            appDescription = Files.readAllLines(path).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (people.isEmpty()) {
            System.out.println("No people in system");
            System.exit(0);
        }
    }
    
    public Account currentAccount() {
        return account_service.currentAccount();
    }
}

@Stateless
class PersonService {

    @Inject
    Dao dao;
    
    public List<Person> findAll() {
        return dao.findAll();
    }
}

@Stateless
class AccountService {

    @Resource
    private SessionContext ctx;
    
    @Inject
    Dao dao;
    
    private Account account;
    
    public Account currentAccount() {
        if (account == null) {
            this.account = dao.findAccountByName(ctx.getCallerPrincipal().getName());
        }
        return this.account;
    }
}

class Dao {
    
    @PersistenceContext
    EntityManagerFactory entityManager;
    
    public List<Person> findAll() {
        return entityManager.createEntityManager().createQuery("select * from Person", Person.class).getResultList();
    }
    
    public Account findAccountByName(String name) {
        return entityManager.createEntityManager().createQuery("select a from Account a where a.name like " + name + " and valid = 1", Account.class).getSingleResult();
    }
    
    public boolean hasBirthDay(Person person) {
        return person.getBirthDay() != null;
    }
}

@Getter
@Setter
@Entity
@Table(name = "person")
class Person {

    @Id
    private Long id;
    
    @Column
    private String fullName;
    
    @Column
    private LocalDate birthDay;
    
    @OneToMany(fetch = EAGER)
    private Set<Role> roles;
}

@Getter
@Setter
@Entity
@Table(name = "account")
class Account {

    @Id
    private Long id;
    
    @Column
    private String fullName;
    
    @OneToMany(fetch = EAGER)
    private Set<Role> roles;
}
