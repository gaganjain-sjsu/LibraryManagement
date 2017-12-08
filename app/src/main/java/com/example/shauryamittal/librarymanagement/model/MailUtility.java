package com.example.shauryamittal.librarymanagement.model;

import java.net.PasswordAuthentication;
//import javax.swing.plaf.synth.SynthSpinnerUI;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Created by Harshit on 12/7/17.
 */

public class MailUtility {

    public static void sendMail(String sendTo, String sendText){
        final String username = "librarian.cmpe277@gmail.com";
        final String password = "aghs@123";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("librarian.cmpe277@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(sendTo));
            message.setSubject("Library Management System Notification");
            message.setText(sendText);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }


}

