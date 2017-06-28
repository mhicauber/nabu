package no.rutebanken.nabu.event.email;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.event.NotificationProcessor;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

/**
 * Log notifications to user (for test purposes).
 */
@Service
public class EmailNotificationSender implements NotificationProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailNotificationFormatter formatter;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.from:varsel@entur.no}")
    private String emailFrom;


    @Value("${notification.email.language.default:en}")
    private String emailLanguageDefault;


    @Override
    public void processNotificationsForUser(User user, Set<Notification> notifications) {

        if (user.getContactDetails() == null || user.getContactDetails().getEmail() == null) {
            logger.warn("Unable to notify user without registered email address: " + user.getUsername() + ". Discarding notifications: " + notifications);
            notificationRepository.delete(notifications);
            return;
        }

        logger.info("Sending email to user: " + user.getUsername() + " for notifications: " + notifications);

        Locale locale = new Locale(emailLanguageDefault); // TODO get users default from user

        sendEmail(user.getContactDetails().getEmail(), formatter.getSubject(locale), formatter.formatMessage(notifications, locale));

        notifications.forEach(n -> n.setStatus(Notification.NotificationStatus.COMPLETE));
        notificationRepository.save(notifications);
    }

    protected void sendEmail(String to, String subject, String msg) {
        mailSender.send(mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setText(msg, true);
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setFrom(emailFrom);
        });
    }


    @Override
    public Set<NotificationType> getSupportedNotificationTypes() {
        return Sets.newHashSet(NotificationType.EMAIL, NotificationType.EMAIL_BATCH);
    }
}
