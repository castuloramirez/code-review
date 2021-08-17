import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class PersonView {

    @Inject
    PersonService personService;

    @Inject
    private AccountService accountService;

    @Getter
    @Setter
    private List<Person> people;
    
    @PostConstruct
    public void init() {
        people = personService.findAll();
    }
    
    public Account currentAccount() {
        return accountService.currentAccount();
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
        return entityManager.createNamedQuery("Person.all", Person.class).getResultList();
    }
    
    public Account findAccountByName(String name) {
        return entityManager.createNamedQuery("Person.byName", Person.class)
                      .setParameter("fullName", name)
                      .getSingleResult();
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
}
