package com.lsriders.backend.web.rest;
import com.lsriders.backend.domain.Event;
import com.lsriders.backend.repository.EventRepository;
import com.lsriders.backend.service.dto.EventDTO;
import com.lsriders.backend.web.rest.errors.BadRequestAlertException;
import com.lsriders.backend.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Event.
 */
@RestController
@RequestMapping("/api")
public class EventResource {

    private final Logger log = LoggerFactory.getLogger(EventResource.class);

    private static final String ENTITY_NAME = "event";

    private final EventRepository eventRepository;

    public EventResource(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * POST  /events : Create a new event.
     *
     * @param event the event to create
     * @return the ResponseEntity with status 201 (Created) and with body the new event, or with status 400 (Bad Request) if the event has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) throws URISyntaxException {
        log.debug("REST request to save Event : {}", event);
        if (event.getId() != null) {
            throw new BadRequestAlertException("A new event cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Event result = eventRepository.save(event);
        return ResponseEntity.created(new URI("/api/events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /events : Updates an existing event.
     *
     * @param event the event to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated event,
     * or with status 400 (Bad Request) if the event is not valid,
     * or with status 500 (Internal Server Error) if the event couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/events")
    public ResponseEntity<Event> updateEvent(@RequestBody Event event) throws URISyntaxException {
        log.debug("REST request to update Event : {}", event);
        if (event.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Event result = eventRepository.save(event);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, event.getId().toString()))
            .body(result);
    }

    /**
     * GET  /events : get all the events.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of events in body
     */
//    @GetMapping("/events")
//    public List<Event> getAllEvents() {
//        log.debug("REST request to get all Events");
//        return eventRepository.getEventsOrderByKm();
//    }

    @GetMapping("/events")      //Amb el mapping i el pageable, podem fer una consulta de rutes, i orderby el parametre que vulguem.
    public List<Event> getAllEvents(Pageable pageable) {
        log.debug("REST request to get all Events");
        return eventRepository.findAll(pageable).getContent();
    }

    @GetMapping("/events-dto")      //Amb el mapping i el pageable, podem fer una consulta de rutes, i orderby el parametre que vulguem.
    public List<EventDTO> getAllEventsDTO() {
        log.debug("REST request to get all Events");
        return eventRepository.getEventsByNameKmRouteDesc();
    }

    @GetMapping("/events/dateAfter/")
    public List<Event> getEventsAfter(@RequestParam String dateString) {
        /*
        log.debug("REST request to get Event : {}", date);

        String pattern = "yyyy-MM-dd";
        DateTimeFormatter Parser = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime dateTime = LocalDateTime.parse(date, Parser);

        //ZonedDateTime zonedDateTime = ZonedDateTime.parse(date,Parser);

        ZonedDateTime madrid = dateTime.atZone(ZoneId.of("Europe/Madrid"));
        */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime = LocalDate.parse(dateString, formatter);

        ZonedDateTime resultado = dateTime.atStartOfDay(ZoneId.systemDefault());
        return eventRepository.findByDateAfter(resultado);
    }

    @GetMapping("/events/dateBefore/")
    public List<Event> getEventsBefore(@RequestParam String dateString) {
        //log.debug("REST request to get Event : {}", date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime = LocalDate.parse(dateString, formatter);

        ZonedDateTime resultado = dateTime.atStartOfDay(ZoneId.systemDefault());
        return eventRepository.findByDateBefore(resultado);
    }

    /**
     * GET  /events/:name : get the "name" event.
     *
     * @param name the id of the event to retrieve
     * @return nameList que es el nom de la ruta
     *
     **/
    @GetMapping("/events/by-Name/{name}")   //query que retorna el nom de una ruta en concret, juntament amb els detalls de la ruta
    public List<Event> getByName(@PathVariable String name){
        List<Event> nameList = eventRepository.getEventsByName(name);
        return nameList;
    }


    /**
     * GET  /events/:id : get the "id" event.
     *
     * @param id the id of the event to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the event, or with status 404 (Not Found)
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        log.debug("REST request to get Event : {}", id);
        Optional<Event> event = eventRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(event);
    }

    /**
     * DELETE  /events/:id : delete the "id" event.
     *
     * @param id the id of the event to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.debug("REST request to delete Event : {}", id);
        eventRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
