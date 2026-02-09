package com.server.controller;


import com.server.exception.DaoException;
import com.server.model.PrayerRequest;
import com.server.model.PrayerRequestDto;
import com.server.service.PrayerRequestService;
import com.server.service.PrayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;


@CrossOrigin
@RequestMapping("/api/prayer-requests")
@RestController
public class PrayerRequestController {

    private final PrayerRequestService prayerRequestService;

    public PrayerRequestController (PrayerRequestService prayerRequestService){
        this.prayerRequestService = prayerRequestService;
    }

    @GetMapping("")
    public List<PrayerRequestDto> listPrayerRequest (Principal principal, @RequestParam(required = false) String category, @RequestParam(required = false) Boolean isAnswered){
        List<PrayerRequestDto> prayerRequestDtos;

        try{
            prayerRequestDtos = prayerRequestService.getAllPrayerRequestDto(principal, category, isAnswered);
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
        return prayerRequestDtos;
    }

    @GetMapping("/{id}")
    public PrayerRequest getPrayerRequest(@PathVariable int id){
        PrayerRequest prayerRequest;

        try{
            prayerRequest = prayerRequestService.getPrayerRequestById(id);
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
        return prayerRequest;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public PrayerRequest createPrayerRequest(@Valid @RequestBody PrayerRequest newPrayerRequest, Principal principal){

        PrayerRequest prayerRequest = null;

        try{
            prayerRequest = prayerRequestService.createPrayerRequest(newPrayerRequest, principal);
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
        return prayerRequest;

    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public PrayerRequest updatePrayerRequest(@PathVariable int id, @Valid @RequestBody PrayerRequest prayerRequest, Principal principal){
        prayerRequest.setId(id);
        try{
            return prayerRequestService.updatePrayerRequest(prayerRequest, principal);
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePrayerRequest(@PathVariable int id, Principal principal){

        try{
            int deletedRows = prayerRequestService.deletePrayerRequest(id, principal);
            if (deletedRows == 0){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prayer request not found"); //Status Code: 404 = The given URL doesn't point to a valid resource
            }
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }

    }

}
