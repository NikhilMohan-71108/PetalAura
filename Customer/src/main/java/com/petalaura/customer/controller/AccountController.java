package com.petalaura.customer.controller;


import com.itextpdf.text.DocumentException;
import com.petalaura.library.Service.AddressService;
import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.OrderService;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.dto.AddressDto;
import com.petalaura.library.model.Address;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.OrderDetails;
import com.petalaura.library.utils.InvoiceGeneratorPdf;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class AccountController {

    @Autowired
    CustomerService customerService;

    @Autowired
    OrderService orderService;

    @Autowired
    AddressService addressService;

    @Autowired
    WalletService walletService;

    @GetMapping("/account")
    public String showAccount(Model model, Principal principal) {
         if (principal == null) {
                  return "redirect:/login";
         }

         String username = principal.getName();
         Customer customer=customerService.findByEmail(username);
         model.addAttribute("customer",customer);
        model.addAttribute("address",customer.getAddress());
        List<OrderDetails> orders = orderService.findOrderDetailsByCustomer(username);
        model.addAttribute("orders", orders);
        List<Long> orderIds = new ArrayList<>();
        for (Address address : customer.getAddress()) {
            List<Long> addressOrderIds = orderService.findOrderIdsByAddressId(address.getId());
            orderIds.addAll(addressOrderIds);
        }
        model.addAttribute("orderIds", orderIds);
        return "account";
    }

    @PostMapping("/saveAddress")
    public String saveAddress(Model model, Principal principal, HttpSession session,AddressDto addressDto) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        addressService.save(addressDto, username);
        return "redirect:/account";
    }

    @PostMapping("/saveAddressAccount")
    public String showSaveAccountAddress(@Valid @ModelAttribute("address")AddressDto addressDto,
                                         BindingResult result, HttpSession session,
                                         Principal principal) {

        if(result.hasErrors()){
            session.removeAttribute("error");
            return "add-address";
        }
        String username = principal.getName();
        addressService.save(addressDto,username);
        return "redirect:/account";
    }

    @GetMapping("/cancelOrder")
    public String cancelOrder(@RequestParam("orderId")long id){
      orderService.cancelOrder(id);
      return "redirect:/account";
    }

    @GetMapping("/deleteCustomerAddress")
    public String deleteCustomerAddress(@RequestParam("addressId")Long id,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {

        addressService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Address deleted successfully");

        return "redirect:/account";
    }
    @PostMapping("/updateAccountAccount")
    public String showUpdateCustomerAccount(@ModelAttribute("customer") Customer customer,
                                            @RequestParam("email") String email,
                                            @RequestParam("name") String name,
                                            @RequestParam("mobile") Long mobile) {
        customerService.update(email,name,mobile);
        return "redirect:/account";
    }

    @GetMapping("/editCustomerAddress")
    public String showEditCustomerAddress(@RequestParam("addressId")Long id,Model model){
        Optional<Address> address=addressService.findByid(id);
        model.addAttribute("address",address);
        return "edit-address";
    }
     @GetMapping("/editCustomerDetails")
     public String showEditCustomerDetails(@RequestParam("email")String email,Model model){
        Customer customer=customerService.findByEmail(email);
        model.addAttribute("customer",customer);
        return "edit-account";
     }
    @GetMapping("/accountAddAddress")
    public String showAccountAddAddress(@RequestParam(value = "address_id", required = false) Long addressId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        AddressDto addressDto = new AddressDto();
        model.addAttribute("address", addressDto);
        return "add-address";
    }

    @GetMapping("/returnOrder")
    public String showReturnOrder(@RequestParam(value = "order_id", required = false) Long id)

                                  {

        Order order=orderService.findById(id);
        //orderService.updateReturnMessage(orderId, returnMessage);
        orderService.returnOrder(id);
        if(order.getPaymentMethod().equals("online_payment") || order.getPaymentMethod().equals("wallet"))
       {
           // walletService.addToRefundAmount(id);
       }
       return "redirect:/account";
    }
    @GetMapping("/orderDetails/{orderId}")
    public String getOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
        Order order=orderService.findById(orderId); // Get the order details from the service
        model.addAttribute("orderDetail", order);  // Add it to the model
        return "account";  // Return the Thymeleaf template name
    }

    @PostMapping("/returnOrder/{orderId}")
    public String submitReturnMessage(@PathVariable("orderId") Long orderId,
                                      @RequestParam("return") String returnMessage,
                                      Model model) {
        // Call the service to update the return message for the order
        orderService.updateReturnMessage(orderId, returnMessage);

        // Optionally add a message to the model (to display a success message or something else)
        model.addAttribute("message", "Return message submitted successfully.");

        // Redirect to the order details page or wherever you'd like
        return "redirect:/orderDetails/" + orderId;  // Adjust as needed
    }


    @GetMapping("/generateInvoice")
    public void generateInvoicePdf(@RequestParam("orderId") Long orderId, HttpServletResponse response, Principal principal) throws  IOException, DocumentException {
        String email = principal.getName();
        Order order = orderService.findOrderByIdAndCustomerEmail(orderId, email);

        if (order == null) {
            // Handle no order found for the given id and customer email
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        InvoiceGeneratorPdf invoiceGenerator = new InvoiceGeneratorPdf(order);
        invoiceGenerator.generate(response);
    }


}


