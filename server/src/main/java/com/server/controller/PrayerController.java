package com.server.controller;


import com.server.exception.DaoException;
import com.server.model.Prayer;
import com.server.service.PrayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@CrossOrigin
@RequestMapping("/api/prayers")
@RestController
public class PrayerController {

    private final PrayerService prayerService;

    public PrayerController(PrayerService prayerService){
        this.prayerService = prayerService;
    }

    @GetMapping("")
    public List<Prayer> listPrayers (Principal principal){
        List<Prayer> prayers;

        try{
            prayers = prayerService.getAllPrayers(principal);
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
        return prayers;
    }

    @GetMapping("/public")
    public List<Prayer> listPublicPrayers(){
        try {
            return prayerService.getAllPublicPrayers();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Prayer getPrayerById(@PathVariable int id){
        Prayer prayer;

        try{
           prayer = prayerService.getPrayerById(id);
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
        return prayer;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Prayer createPrayer(@Valid @RequestBody Prayer newPrayer, Principal principal){
        Prayer prayer  = null;

        try{
            prayer = prayerService.createPrayer(newPrayer, principal);
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
        return prayer;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePrayer(@PathVariable int id, Principal principal){

        try {
            int deletedRows = prayerService.deletePrayer(id, principal);
            if (deletedRows == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prayer request not found"); //Status Code: 404 = The given URL doesn't point to a valid resource
            }
        }catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
        }

    }


