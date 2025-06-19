package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.Appointment;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;

    public NotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;
    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;
    @Value("${twilio.from-phone:}")
    private String twilioFromPhone;

    private boolean twilioInitialized = false;

    @PostConstruct
    private void initTwilio() {
        if (!twilioAccountSid.isEmpty() && !twilioAuthToken.isEmpty()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            twilioInitialized = true;
        }
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.debug("Sending email to {} with subject {}", to, subject);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // ignore email failures
        }
    }

    @Override
    public void sendSms(String to, String message) {
        logger.debug("Sending SMS to {}", to);
        if (!twilioInitialized || twilioFromPhone.isEmpty()) {
            return;
        }
        try {
            Message.creator(new PhoneNumber(to), new PhoneNumber(twilioFromPhone), message).create();
        } catch (Exception e) {
            // ignore sms failures
        }
    }

    @Override
    public void sendAppointmentReminder(Appointment appointment) {
        String subject = "Appointment Reminder";
        String body = "Reminder: you have an appointment on " + appointment.getAppointmentTime()
                + " for service " + appointment.getService().getName() + ".";
        sendEmail(appointment.getCustomer().getEmail(), subject, body);
        sendSms(appointment.getCustomer().getPhone(), body);
    }
}
