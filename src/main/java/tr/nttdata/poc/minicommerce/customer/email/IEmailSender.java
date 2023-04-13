package tr.nttdata.poc.minicommerce.customer.email;


import tr.nttdata.poc.minicommerce.customer.model.Customer;

public interface IEmailSender {
    String send(EmailSubject subject, Customer customer);
}