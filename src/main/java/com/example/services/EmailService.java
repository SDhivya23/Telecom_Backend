package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationService notificationService;


    public void sendOtpEmail(String email, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("dhivyajaga2004@gmail.com");
        msg.setTo(email);
        msg.setSubject("Your OTP for Registration");
        msg.setText("Your OTP is: " + otp);

        mailSender.send(msg);
        System.out.println("✅ OTP Email sent to " + email);
    }

    public void sendWelcomeEmail(String email, String name, Integer userId) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Welcome");
        msg.setText("Hello " + name + ", your account is created.");

        mailSender.send(msg);


        notificationService.notifyUser(
                userId,
                "✅ Welcome " + name + "! Account created successfully"
        );
    }

    public void sendTicketCreatedEmail(String email, Integer ticketId, Integer userId) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Ticket Created");
        msg.setText("Your ticket ID " + ticketId + " is created.");

        mailSender.send(msg);


        notificationService.notifyUser(
                userId,
                "✅ Ticket created successfully. ID: " + ticketId
        );
    }


    public void sendTicketAssignedEmail(String email, String name, Integer ticketId, Integer userId) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Task Assigned");
        msg.setText("Ticket ID " + ticketId + " assigned to you.");

        mailSender.send(msg);

        notificationService.notifyUser(
                userId,
                "📌 New task assigned. Ticket ID: " + ticketId
        );
    }

    public void sendCompletedEmail(String email, Integer userId) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Completed");
        msg.setText("Your issue is resolved.");

        mailSender.send(msg);

        notificationService.notifyUser(
                userId,
                "✅ Your issue has been resolved"
        );
    }

    public void sendFailedEmailToAdmin(String adminEmail,
                                       Integer ticketId,
                                       Integer adminUserId) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(adminEmail);
        msg.setSubject("⚠️ Ticket Failed");
        msg.setText("Ticket ID " + ticketId + " has FAILED.");

        mailSender.send(msg);

        notificationService.notifyUser(
                adminUserId,
                "❗ Ticket ID " + ticketId + " has FAILED"
        );

        System.out.println("✅ Failed mail + notification sent ✅");
    }


    public void sendDeferredEmailToAdmin(String adminEmail,
                                         Integer ticketId,
                                         Integer adminUserId) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(adminEmail);
        msg.setSubject("⚠️ Ticket Deferred");
        msg.setText("Ticket ID " + ticketId + " is DEFERRED.");

        mailSender.send(msg);

        notificationService.notifyUser(
                adminUserId,
                "⚠️ Ticket ID " + ticketId + " is DEFERRED"
        );

        System.out.println("✅ Deferred mail + notification sent ✅");
    }

    public void sendProfileUpdateEmail(String email, String name, Integer userId) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Profile Updated ✅");
        msg.setText("Hello " + name + ",\n\nYour profile has been updated successfully.");

        mailSender.send(msg);

        notificationService.notifyUser(
                userId,
                "✅ Your profile has been updated successfully"
        );

        System.out.println("✅ Profile update email + notification sent ✅");
    }

    public void sendHazardAwareAssignmentEmail(String email,
                                               String name,
                                               Integer ticketId,
                                               Integer userId,
                                               String hazardMessage) {

        String fullMessage =
                "📌 New Task Assigned\n" +
                        "Ticket ID: " + ticketId + "\n\n" +
                        hazardMessage;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("⚠ Task Assigned with Hazard Alert");
        msg.setText(fullMessage);

        mailSender.send(msg);

        notificationService.notifyUser(userId, fullMessage);
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        mailSender.send(msg);
    }

    public void sendPasswordResetEmail(String to, String name) {

        String subject = "Password Updated Successfully ✅";

        String body = "Hello " + name + ",\n\n" +
                "Your password has been changed successfully.\n" +
                "If you did not perform this action, please contact support immediately.\n\n" +
                "Thank you,\nSTS Team";

        sendEmail(to, subject, body);
    }

}
