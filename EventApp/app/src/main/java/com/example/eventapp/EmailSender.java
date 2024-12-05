package com.example.eventapp;
import android.os.AsyncTask;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;

public class EmailSender extends AsyncTask<Void, Void, Void> {

    String activationPageHtml = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Aktivacija naloga</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>Successfully finished registration.</h1>\n" +
            "    <p> Now you can login and start using Event Planner!</p>\n" +
            "</body>\n" +
            "</html>";
    private static final String EMAIL = "medicinskaopremaisa@gmail.com";
    private static final String PASSWORD = "xqxbjlapbjmiygcy";
    private String RECIPIENT_EMAIL; // Email primaoca

    private String EMAIL_BODY;

    public EmailSender(String recipientEmail, String emailBody) {
        this.RECIPIENT_EMAIL = recipientEmail;
        this.EMAIL_BODY = emailBody;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        sendEmail();
        return null;
    }

    private void sendEmail() {

        String activationCode = generateActivationCode();

        // istek koda
        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.HOUR_OF_DAY, 24);
        // Konfiguracija SMTP servera
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        // Kreiranje sesije
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECIPIENT_EMAIL));
            message.setSubject("Rejecting owner registration request");
             message.setContent("Reason for rejecting: " + EMAIL_BODY, "text/html");

            // Slanje emaila
            Transport.send(message);

            System.out.println("The email was sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateActivationCode() {
        return UUID.randomUUID().toString();
    }
}
