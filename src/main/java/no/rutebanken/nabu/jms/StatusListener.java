package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.jms.mapper.EventMapper;
import no.rutebanken.nabu.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
// Remove when marduk is updated to send on Event format
@Component
public class StatusListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    EventRepository eventRepository;

    private EventMapper eventMapper = new EventMapper();

    @JmsListener(destination = "ExternalProviderStatus")
    public void processMessage(String content) {
        Status status = Status.fromString(content);
        logger.info("Received job status update: " + status.toString());

        eventRepository.save(eventMapper.toJobEvent(status));
    }

}
