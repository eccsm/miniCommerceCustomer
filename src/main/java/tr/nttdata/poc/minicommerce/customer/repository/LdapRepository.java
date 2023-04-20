package tr.nttdata.poc.minicommerce.customer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Repository;
import tr.nttdata.poc.minicommerce.customer.model.Customer;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Repository
public class LdapRepository {

    @Autowired
    private LdapTemplate ldapTemplate;

    public Customer getCustomerByEmail(String email) {
        List<Customer> people = ldapTemplate.search(query().where("uid").is(email), new CustomerAttributesMapper());
        return ((null != people && !people.isEmpty()) ? people.get(0) : null);
    }

    public void addCustomer(Customer customer) {

        Name dn = LdapNameBuilder
                .newInstance()
                .add("cn", customer.getEmail())
                .build();

        DirContextAdapter context = new DirContextAdapter(dn);

        context.setAttributeValues(
                "objectclass",
                new String[]
                        { "top",
                                "inetOrgPerson" });
        context.setAttributeValue("cn", customer.getFirstName());
        context.setAttributeValue("sn", customer.getLastName());
        context.setAttributeValue("uid", customer.getEmail());
        context.setAttributeValue
                ("userPassword", customer.getPassword());

        ldapTemplate.bind(context);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {

        authenticate(username,oldPassword);

        Name dn = LdapNameBuilder.newInstance()
                .add("cn", username)
                .build();
        DirContextOperations context
                = ldapTemplate.lookupContext(dn);


        context.setAttributeValue
                ("userPassword", newPassword);

        ldapTemplate.modifyAttributes(context);
    }

    public boolean authenticate(String username, String password) {
        try {
            ldapTemplate.authenticate(query().where("uid").is(username), password);
        } catch (Exception e) {
            return false;
        }
        return true;
     }

    private class CustomerAttributesMapper implements AttributesMapper<Customer> {
        public Customer mapFromAttributes(Attributes attrs) throws NamingException {
            Customer person = new Customer();
            person.setEmail(null != attrs.get("uid") ? (String) attrs.get("uid").get() : null);
            person.setFirstName((String) attrs.get("cn").get());
            person.setLastName((String) attrs.get("sn").get());
            return person;
        }
    }
    private class PersonNameAttributesMapper implements AttributesMapper<String> {
        public String mapFromAttributes(Attributes attrs) throws NamingException {
            return attrs.get("cn").get().toString();
        }
    }
}
