import lombok.Getter;
import lombok.Setter;

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
        appDescription = Files.readAllLines(path).get(0);
            
        if (people.isEmpty()) {
            System.out.println("No people in system");
            System.exit();
        }
    }
    
    public Account currentAccount() {
        return account_service.currentAccount();
    }
}

@Stateless
public class PersonService {

    @Inject
    Dao dao;
    
    public List<Person> findAll() {
        return dao.findAll();
    }
}

@Stateless
public class AccountService {

    @Resource private SessionContext ctx;
    
    @Inject
    Dao dao;
    
    private Account account;
    
    public Account currentAccount() {
        if (account == null) {
            this.account = personDao.findAccountByName(ctx.getCallerPrincipal().getName());
        }
        return this.account;
    }
}

public class Dao {
    
    @PersistenceContext
    EntityManagerFactory entityManager;
    
    public List<Person> findAll() {
        return entityManager.createQuery("select * from Person", Person.class).getResultList();
    }
    
    public Account findAccountByName(String name) {
        return entityManager.createQuery("select a from Account a where a.name like " + name + " and valid = 1", Account.class).getSingleResult();
    }
    
    public boolean hasBirthDay(Person person) {
        return person.getBirthDay() != null;
    }
}

@Getter
@Setter
@Entity
@Table(name = "person")
public class Person {

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
public class Account {

    @Id
    private Long id;
    
    @Column
    private String fullName;
    
    @OneToMany(fetch = EAGER)
    private Set<Role> roles;
}
