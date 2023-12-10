package edu.es.suite;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.es.jre.suite.entity.Goal;
import com.es.jre.suite.entity.Milestone;
import com.es.jre.suite.entity.Privilege;
import com.es.jre.suite.entity.Product;
import com.es.jre.suite.entity.Role;
import com.es.jre.suite.entity.Transaction;
import com.es.jre.suite.entity.User;
import com.es.jre.suite.repository.MilestoneRepository;
import com.es.jre.suite.repository.PrivilegeRepository;
import com.es.jre.suite.repository.ProductRepository;
import com.es.jre.suite.repository.RoleRepository;
import com.es.jre.suite.repository.TransactionRepository;
import com.es.jre.suite.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	boolean alreadySetup = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PrivilegeRepository privilegeRepository;

	@Autowired
	private MilestoneRepository milestoneRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
 
        if (alreadySetup) return;
        
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
 
        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        User userAdmin = new User();
        userAdmin.setName("AdminUserTest");
        userAdmin.setSurname("AdminUserTestSurname");
        userAdmin.setUsername("AdminUserTestUsername");
        userAdmin.setPassword("1111");
        userAdmin.setMail("admin@test.com");
        userAdmin.setRoles(Arrays.asList(adminRole));
        userAdmin.setEnabled(true);
        userRepository.save(userAdmin); 

        Role userRole = roleRepository.findByName("ROLE_USER");
        User user = new User();
        user.setName("UserTest");
        user.setSurname("UserTestSurname");
        user.setUsername("UserTestUsername");
        user.setPassword("2222");
        user.setMail("user@test.com");
        user.setRoles(Arrays.asList(userRole));
        user.setEnabled(true);
        userRepository.save(user);

        Milestone milestone= new Milestone();
        milestone.setName("Initial Milestone");
        milestone.setProgress(50);
        milestone.setStart(LocalDateTime.now());
        milestone.setEnd(LocalDateTime.now().plusDays(14));
        
        milestone.setUsers(Arrays.asList(userAdmin,user));
        userAdmin.setMilestones(Arrays.asList(milestone));
        user.setMilestones(Arrays.asList(milestone));
        
        Goal goalOne= Goal.builder().build();
        goalOne.setDescription("Descripcion objetivo 1");
        goalOne.setName("objetivo 1");
        goalOne.setProgress(0);
        goalOne.setMilestone(milestone);
        goalOne.setUser(userAdmin);
        
        Goal goalTwo= Goal.builder().build();
        goalTwo.setDescription("Descripcion objetivo 2");
        goalTwo.setName("objetivo 2");
        goalTwo.setProgress(50);
        goalTwo.setMilestone(milestone);
        goalTwo.setUser(user);
        
        milestone.setGoals(Arrays.asList(goalOne,goalTwo));
        
        milestoneRepository.save(milestone);    
        
        Product product1=Product.builder().build();
        product1.setName("Glasses");
        product1.setDescription("TOP glasses");
        product1.setCode("NORMALBVBB");
        
        Product product2=Product.builder().build();
        product2.setName("Sun Glasses");
        product2.setDescription("TOP Sunglasses");
        product2.setCode("SUNAAAABBB");
        
        productRepository.save(product1);
        productRepository.save(product2);
        
        Transaction trx1= Transaction.builder().build();
        trx1.setTotal(3);
        trx1.setDone(0);
        trx1.setType("SELL");
        trx1.setProduct(product1);
        trx1.setGoal(goalOne);
  
        Transaction trx2= Transaction.builder().build();
        trx2.setTotal(5);
        trx2.setDone(1);
        trx2.setType("SELL");
        trx2.setProduct(product2);
        trx2.setGoal(goalTwo);  
        
        Transaction trx3= Transaction.builder().build();
        trx3.setTotal(10);
        trx3.setDone(2);
        trx3.setType("SELL");
        trx3.setProduct(product1);
        trx3.setGoal(goalTwo);  
        
        product1.setTransactions(Arrays.asList(trx1,trx3));
        product2.setTransactions(Arrays.asList(trx2));
        
        goalOne.setTransactions(Arrays.asList(trx1));
        goalTwo.setTransactions(Arrays.asList(trx2,trx3));      
        
        transactionRepository.save(trx1);        
        transactionRepository.save(trx2);
        transactionRepository.save(trx3);
        
        alreadySetup = true;
    }

	@Transactional
	Privilege createPrivilegeIfNotFound(String name) {

		Privilege privilege = privilegeRepository.findByName(name);
		if (privilege == null) {
			privilege = Privilege.builder().name(name).build();
			privilegeRepository.save(privilege);
		}
		return privilege;
	}

	@Transactional
	Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

		Role role = roleRepository.findByName(name);
		if (role == null) {
			role = Role.builder().name(name).build();
			role.setPrivileges(privileges);
			roleRepository.save(role);
		}
		return role;
	}
}
