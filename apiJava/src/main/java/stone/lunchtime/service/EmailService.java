// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import stone.lunchtime.service.exception.SendMailException;

/**
 * Email service.
 */
@Service
public class EmailService {
	private static final Logger LOG = LogManager.getLogger();
	@Autowired
	private JavaMailSender emailSender;

	/** Option used to disable send mail */
	@Value("${configuration.allow.sendmail}")
	private boolean allowSendmail;

	/**
	 * Sends an email.
	 *
	 * @param pTo      to address
	 * @param pSubject subject of the mail
	 * @param pText    body of the email
	 * @throws SendMailException if mail was not sent
	 */
	public void sendSimpleMessage(String pTo, String pSubject, String pText) throws SendMailException {
		EmailService.LOG.debug("sendSimpleMessage - {} {} {}", pTo, pSubject, pText);
		if (this.allowSendmail) {
			try {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(pTo);
				message.setSubject(pSubject);
				message.setText(pText);
				this.emailSender.send(message);
				EmailService.LOG.debug("sendSimpleMessage - Message sent to {}", pTo);
			} catch (Exception lExp) {
				throw new SendMailException("Erreur lors de l'envoie de l'email", lExp);
			}
		} else {
			EmailService.LOG.fatal("sendSimpleMessage - OK BUT send mail is deactivated (see configuration)");
		}
	}

	/**
	 * Activates send mail.
	 */
	public void activateSendMail() {
		this.allowSendmail = true;
	}

	/**
	 * Deactivate send mail.
	 */
	public void deactivateSendMail() {
		this.allowSendmail = false;
	}

	/**
	 * Indicates status for send mail.
	 * 
	 * @return true if mail can be sent, false if not
	 */
	public boolean getSendMail() {
		return this.allowSendmail;
	}
}
